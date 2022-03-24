package org.wyona.webapp.services;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

import org.springframework.core.io.ClassPathResource;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class JwtService {

    @Autowired
    public JwtService() {
    }

    /**
     * Get public key to validate JWT
     */
    private RSAPublicKey getPublicKey() throws Exception {
        String publicKeyContent = readString(new ClassPathResource("jwt/public_key.pem").getInputStream());

        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        log.debug("Public key: " + publicKeyContent);

        java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

        return pubKey;
    }

    /**
     * Convert InputStream to String
     * @param inputStream
     * @return
     * @throws Exception
     */
    private String readString(java.io.InputStream inputStream) throws Exception {

        java.io.ByteArrayOutputStream into = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        for (int n; 0 < (n = inputStream.read(buf));) {
            into.write(buf, 0, n);
        }
        into.close();
        return new String(into.toByteArray());
    }

    /**
     * https://auth0.com/docs/tokens/access-tokens/validate-access-tokens
     * @param publicKey Custom public key
     */
    public boolean isJWTValid(String jwtToken, java.security.PublicKey publicKey) {
        log.info("Check signature and expiry date of JWT token ...");
        try {
            if (publicKey == null) {
                log.info("Use default public key ...");
                publicKey = getPublicKey();
            }
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jwtToken);
            log.info("JWT is valid :-)");
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid:" + e.getMessage());
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @param claim Claim Id, e.g. "kid"
     */
    public String getHeaderValue(String jwtToken, String claim) {
        log.info("Get header value for '" + claim + "' ...");
        String header = getChunk(jwtToken,0);
        log.info("JWT header: " + header);
        return parseChunk(header, claim);
    }

    /**
     * @param claim Claim Id, e.g. "aud"
     */
    public String getPayloadValue(String jwtToken, String claim) {
        log.info("Get payload value for '" + claim + "' ...");
        String payload = getChunk(jwtToken, 1);
        log.info("JWT payload: " + payload);
        return parseChunk(payload, claim);
    }

    /**
     *
     */
    private String getChunk(String jwtToken, int i) {
        String[] chunks = jwtToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return new String(decoder.decode(chunks[i]));
    }

    /**
     *
     */
    private String parseChunk(String chunk, String claim) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode headerNode = mapper.readTree(chunk);
            return headerNode.get(claim).asText();
        } catch(Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }
}
