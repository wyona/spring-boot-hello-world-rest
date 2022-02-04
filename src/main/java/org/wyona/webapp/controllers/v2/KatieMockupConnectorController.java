package org.wyona.webapp.controllers.v2;

import io.swagger.annotations.*;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.wyona.webapp.models.LanguageEmail;

import lombok.extern.slf4j.Slf4j;
import org.wyona.webapp.models.katie.Domain;
import org.wyona.webapp.models.katie.QnA;

/**
 * 'Katie Mockup Connector' Controller
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v2")
@AllArgsConstructor
public class KatieMockupConnectorController implements KatieConnectorController {

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#createTenant(Domain)
     */
    @PostMapping("/tenant")
    @ApiOperation(value = "Create tenant")
    public ResponseEntity<String> createTenant(@RequestBody Domain domain) {
        log.info("TODO: Create tenant associated with Katie domain ID '" + domain.getId() + "' ...");
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#deleteTenant(String)
     */
    @DeleteMapping("/tenant/{domain-id}")
    @ApiOperation(value = "Delete tenant")
    public ResponseEntity<?> deleteTenant(
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId
    ){
        log.info("TODO: Delete tenant associated with Katie domain ID '" + domainId + "' ...");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#train(QnA)
     */
    @PostMapping("/qna")
    @ApiOperation(value = "Add QnA")
    public ResponseEntity<String> train(@RequestBody QnA qna) {
        log.info("TODO: Train QnA ...");
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
}
