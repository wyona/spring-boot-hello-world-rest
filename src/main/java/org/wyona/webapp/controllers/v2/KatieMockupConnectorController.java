package org.wyona.webapp.controllers.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import org.wyona.webapp.models.katie.Answer;
import org.wyona.webapp.models.katie.Domain;
import org.wyona.webapp.models.katie.QnA;
import org.wyona.webapp.models.katie.Sentence;
import org.wyona.webapp.services.JwtService;

import technology.semi.weaviate.client.Config;
import technology.semi.weaviate.client.WeaviateClient;
import technology.semi.weaviate.client.v1.data.model.ObjectReference;
import technology.semi.weaviate.client.v1.data.model.WeaviateObject;
import technology.semi.weaviate.client.base.Result;
import technology.semi.weaviate.client.v1.graphql.model.GraphQLResponse;
import technology.semi.weaviate.client.v1.graphql.query.argument.AskArgument;
import technology.semi.weaviate.client.v1.graphql.query.argument.WhereArgument;
import technology.semi.weaviate.client.v1.graphql.query.argument.WhereOperator;
import technology.semi.weaviate.client.v1.graphql.query.fields.Field;
import technology.semi.weaviate.client.v1.graphql.query.fields.Fields;
import technology.semi.weaviate.client.v1.misc.model.Meta;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 'Katie Mockup Connector' Controller
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v2")
//@AllArgsConstructor
public class KatieMockupConnectorController implements KatieConnectorController {

    @Value("${weaviate.host}")
    private String weaviateHost;

    @Value("${weaviate.protocol}")
    private String weaviateProtocol;

    @Value("${weaviate.basic.auth.username}")
    private String basicAuthUsername;

    @Value("${weaviate.basic.auth.password}")
    private String basicAuthPassword;

    @Autowired
    JwtService jwtService;

    private static final String CLAZZ_QUESTION = "Question";
    private static final String FIELD_QUESTION = "question";
    private static final String CLAZZ_ANSWER = "Answer";
    private static final String FIELD_ANSWER = "answer";
    private static final String CLAZZ_TENANT = "Tenant";
    private static final String FIELD_TENANT = "tenant";

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#createTenant(Domain, HttpServletRequest)
     */
    @PostMapping("/tenant")
    @ApiOperation(value = "Create tenant")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer JWT",
                    required = false, dataType = "string", paramType = "header") })
    public ResponseEntity<String> createTenant(@RequestBody Domain domain, HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return createTenantWeaviateImpl(domain);

        //log.info("TODO: Create tenant associated with Katie domain ID '" + domain.getId() + "' ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#deleteTenant(String, HttpServletRequest)
     */
    @DeleteMapping("/tenant/{domain-id}")
    @ApiOperation(value = "Delete tenant")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer JWT",
                    required = false, dataType = "string", paramType = "header") })
    public ResponseEntity<?> deleteTenant(
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId,
            HttpServletRequest request
    ){
        if (!isAuthorized(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return deleteTenantWeaviateImpl(domainId);

        //log.info("TODO: Delete tenant associated with Katie domain ID '" + domainId + "' ...");
        //return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#train(QnA, String, HttpServletRequest)
     */
    @PostMapping("/qna/{domain-id}")
    @ApiOperation(value = "Add QnA")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer JWT",
                    required = false, dataType = "string", paramType = "header") })
    public ResponseEntity<String> train(
            @RequestBody QnA qna,
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId,
            HttpServletRequest request
    ) {
        if (!isAuthorized(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return trainWeaviateImpl(qna, domainId);

        //log.info("TODO: Train QnA ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#getAnswers(Sentence, String, HttpServletRequest)
     */
    @PostMapping("/ask/{domain-id}")
    @ApiOperation(value = "Ask question")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer JWT",
                    required = false, dataType = "string", paramType = "header") })
    public ResponseEntity<Answer[]> getAnswers(
            @RequestBody Sentence question,
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId,
            HttpServletRequest request
    ) {
        if (!isAuthorized(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return getAnswersWeaviateImpl(question, domainId);

        //log.info("TODO: Get answers to question '" + question.getText() + "' ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#deleteQnA(String, String, HttpServletRequest)
     */
    @DeleteMapping("/qna/{domain-id}/{uuid}")
    @ApiOperation(value = "Delete QnA")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer JWT",
                    required = false, dataType = "string", paramType = "header") })
    public ResponseEntity<?> deleteQnA(
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId,
            @ApiParam(name = "uuid", value = "UUID of QnA", required = true)
            @PathVariable(name = "uuid", required = true) String uuid,
            HttpServletRequest request
    ) {
        if (!isAuthorized(request)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return deleteQnAWeaviateImpl(domainId, uuid);

        //log.info("TODO: Delete QnA '" + uuid + "' ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * Check authorization of request
     * @return true when authorized and false otherwise
     */
    private boolean isAuthorized(HttpServletRequest request) {
        String jwtToken = getJWT(request);
        log.info("Issuer: " + jwtService.getPayloadValue(jwtToken, "iss"));
        // TODO: Retrieve public key from https://ukatie.com/swagger-ui/#/authentication-controller/getJWTPublicKeyUsingGET
        if (jwtService.isJWTValid(jwtToken, null)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    private ResponseEntity<Answer[]> getAnswersWeaviateImpl(Sentence question, String domainId) {
        List<UuidCertainty> idsFromQuestions = new ArrayList<UuidCertainty>();
        idsFromQuestions = getObjectsWeaviateImpl(question, domainId, CLAZZ_QUESTION, FIELD_QUESTION);

        List<UuidCertainty> idsFromAnswers = new ArrayList<UuidCertainty>();
        idsFromAnswers = getObjectsWeaviateImpl(question, domainId, CLAZZ_ANSWER, FIELD_ANSWER);
        for (UuidCertainty idFromAnswer: idsFromAnswers) {
            idsFromQuestions = mergeWithoutSorting(idsFromQuestions, idFromAnswer);
        }

        Collections.sort(idsFromQuestions, UuidCertainty.CertaintyComparator);

        List<Answer> answers = new ArrayList<Answer>();
        for (UuidCertainty id: idsFromQuestions) {
            log.info("QnA: " + id.getUuid() + " / " + id.getCertainty());
            Answer answer = new Answer();
            answer.setUuid(id.getUuid());
            answers.add(answer);
        }

        return new ResponseEntity<>(answers.toArray(new Answer[0]), HttpStatus.OK);
    }

    /**
     * Merge UUID into existing list of UUIDs, but without sorting by certainty
     * @param uuids Existing list of UUIDs
     * @param uuid UUID to be merged into existing list
     * @return merged list of UUIDs
     */
    private List<UuidCertainty> mergeWithoutSorting(List<UuidCertainty> uuids, UuidCertainty uuid) {
        log.info("Merge uuid '" + uuid.getUuid() + " / "+ uuid.getCertainty() + "' with existing list ...");

        List<UuidCertainty> merged = new ArrayList<UuidCertainty>();

        boolean alreadyAdded = false;
        for (UuidCertainty current: uuids) {
            if (current.getUuid().equals(uuid.getUuid())) {
                if (uuid.getCertainty() > current.getCertainty()) {
                    merged.add(uuid);
                } else {
                    merged.add(current);
                }
                alreadyAdded = true;
            } else {
                merged.add(current);
            }
        }
        if (!alreadyAdded) {
            merged.add(uuid);
        }

        return merged;
    }

    /**
     * https://weaviate.io/developers/weaviate/current/client-libraries/java.html#authentication
     */
    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>() { {
            if (basicAuthUsername != null && basicAuthUsername.length() > 0) {
                log.info("Use Basic Auth ...");
                String auth = basicAuthUsername + ":" + basicAuthPassword;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                put("Authorization", authHeader);
            }
        } };

        return headers;
    }

    /**
     *
     */
    private List<UuidCertainty> getObjectsWeaviateImpl(Sentence question, String domainId, String clazzName, String fieldName) {
        log.info("Weaviate Impl: Get answers to question '" + question.getText() + "' associated with Katie domain ID '" + domainId + "' ...");

        Config config = new Config(weaviateProtocol, weaviateHost, getHeaders());
        WeaviateClient client = new WeaviateClient(config);

        Field idField = Field.builder().name("id").build();
        Field certaintyField = Field.builder().name("certainty").build();
        Field[] subFields = new Field[2];
        subFields[0] = idField;
        subFields[1] = certaintyField;
        Field additonalField = Field.builder().name("_additional").fields(subFields).build();

        Field questionField = Field.builder().name(fieldName).build();
        Field uuidField = Field.builder().name("qnaId").build();
        Fields fields = Fields.builder().fields(new Field[]{ questionField, uuidField, additonalField }).build();

        Float certaintyThreshold = Float.parseFloat("0.5");
        AskArgument askArgument = AskArgument.builder().question(question.getText()).certainty(certaintyThreshold).build();

        log.info("Restrict search to knowledge base with domain Id '" + domainId + "'.");
        String[] path = {FIELD_TENANT, CLAZZ_TENANT, "id"};
        WhereArgument whereArgument = WhereArgument.builder().
                operator(WhereOperator.Equal).
                valueString(domainId).
                path(path).
                build();

        // {Get{Question(ask: {question:\"for dinner\",certainty: 0.5}, where: {operator:Equal, valueString:\"e4ff3246-372b-4042-a9e2-d30f612d1244\", path: [\"tenant\", \"Tenant\", \"id\"]}, limit: 10) {question qnaId _additional{certainty id answer {result}}},Answer(ask: {question:\"for dinner\",certainty: 0.5}, where: {operator:Equal, valueString:\"e4ff3246-372b-4042-a9e2-d30f612d1244\", path: [\"tenant\", \"Tenant\", \"id\"]}, limit: 10) {answer qnaId _additional{certainty id answer {result}}}}}

        log.info("Search within class '" + clazzName + "' ...");
        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(clazzName)
                .withAsk(askArgument)
                .withWhere(whereArgument)
                .withFields(fields)
                .withLimit(10)
                .run();

        List<UuidCertainty> ids = new ArrayList<UuidCertainty>();
        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            Object dataObject = result.getResult().getData();
            log.info("Data object: " +  dataObject);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataNode = mapper.valueToTree(dataObject);
            log.info("Received data: " + dataNode);
            if (dataNode != null) {
                JsonNode questionNodes = dataNode.get("Get").get(clazzName);
                if (questionNodes.isArray()) {
                    for (JsonNode qNode : questionNodes) {
                        String qnaId = qNode.get("qnaId").asText();

                        JsonNode additionalNode = qNode.get("_additional");
                        log.info("_additional: " + additionalNode);
                        String id = additionalNode.get("id").asText();
                        double certainty = additionalNode.get("certainty").asDouble();
                        log.info("Certainty of QnA '" + qnaId + "': " + certainty);

                        ids.add(new UuidCertainty(qnaId, certainty));
                    }
                }
            } else {
                log.warn("No data received.");
            }
        }

        return ids;
    }

    /**
     *
     */
    private ResponseEntity<String> deleteTenantWeaviateImpl(String domainId) {
        log.info("Weaviate Impl: Delete tenant associated with Katie domain ID '" + domainId + "' ...");

        // INFO: Delete all referenced Questions and Answers
        String[] referencedObjects = getReferencedQuestionsAndAnswers(domainId);
        if (referencedObjects != null) {
            for (String uuid: referencedObjects) {
                try {
                    deleteObject(uuid);
                } catch(Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else {
            log.info("No referenced Questions or Answers found for domain '" + domainId + "'.");
        }

        if (deleteObject(domainId)) {
            log.info("Tenant '" + domainId + "' has been deleted successfully.");
        } else {
            log.error("Deleting tenant '" + domainId + "' failed!");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get all UUIDs of referenced Questions and Answers of a particular domain
     * @return array of UUIDs of referenced Questions and Answers of a particular domain
     */
    private String[] getReferencedQuestionsAndAnswers(String domainId) {
        List<String> uuids = new ArrayList<String>();

        Config config = new Config(weaviateProtocol, weaviateHost, getHeaders());
        WeaviateClient client = new WeaviateClient(config);

        Field idField = Field.builder().name("id").build();
        Field[] subFields = new Field[1];
        subFields[0] = idField;

        Field additonalField = Field.builder().name("_additional").fields(subFields).build();
        Fields fields = Fields.builder().fields(new Field[]{ additonalField }).build();

        log.info("Restrict query to knowledge base with domain Id '" + domainId + "'.");
        String[] path = {FIELD_TENANT, CLAZZ_TENANT, "id"};
        WhereArgument whereArgument = WhereArgument.builder().
                operator(WhereOperator.Equal).
                valueString(domainId).
                path(path).
                build();


        log.info("Get all objects with class '" + CLAZZ_QUESTION + "' or '" + CLAZZ_ANSWER + "' linked with domain Id '" + domainId + "' ...");

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(CLAZZ_QUESTION)
                .withWhere(whereArgument)
                .withFields(fields)
                .run();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataNode = mapper.valueToTree(result.getResult().getData());
            log.info("Objects: " + dataNode);

            JsonNode questionNodes = dataNode.get("Get").get(CLAZZ_QUESTION);
            if (questionNodes.isArray()) {
                for (JsonNode question: questionNodes) {
                    JsonNode additionalNode = question.get("_additional");
                    log.info("_additional: " + additionalNode);
                    String id = additionalNode.get("id").asText();
                    uuids.add(id);
                }
            }
        }

        result = client.graphQL().get()
                .withClassName(CLAZZ_ANSWER)
                .withWhere(whereArgument)
                .withFields(fields)
                .run();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataNode = mapper.valueToTree(result.getResult().getData());
            log.info("Objects: " + dataNode);

            JsonNode answersNodes = dataNode.get("Get").get(CLAZZ_ANSWER);
            if (answersNodes.isArray()) {
                for (JsonNode answer: answersNodes) {
                    JsonNode additionalNode = answer.get("_additional");
                    log.info("_additional: " + additionalNode);
                    String id = additionalNode.get("id").asText();
                    uuids.add(id);
                }
            }
        }

        return uuids.toArray(new String[0]);
    }

    /**
     *
     */
    private ResponseEntity<String> deleteQnAWeaviateImpl(String domainId, String uuid) {
        log.info("Weaviate Impl: Delete Qna '" + uuid + "' associated with Katie domain ID '" + domainId + "' ...");

        boolean allDeleted = true;

        String[] questionIds = getQuestionsOrAnswers(uuid, domainId, CLAZZ_QUESTION);
        for (String id: questionIds) {
            if (!deleteObject(id)) {
                allDeleted = false;
            }
        }

        String[] answerIds = getQuestionsOrAnswers(uuid, domainId, CLAZZ_ANSWER);
        for (String id: answerIds) {
            if (!deleteObject(id)) {
                allDeleted = false;
            }
        }

        log.info("All questions and answers deleted: " + allDeleted);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete object stored by Weaviate
     * @param id Object Id
     * @return true when object was deleted and false otherwise
     */
    private boolean deleteObject(String id) {
        Config config = new Config(weaviateProtocol, weaviateHost, getHeaders());
        WeaviateClient client = new WeaviateClient(config);

        Result<Boolean> result = client.data().deleter()
                .withID(id)
                .run();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
            return false;
        } else {
            log.info("Delete object result: " + result.getResult());
            return true;
        }
    }

    /**
     * Get IDs of all Questions or Answers associated with a particular QnA
     * @param clazzName Schema class name, either "Question" or "Answer"
     * @return array of IDs of all Questions or Answers associated with a particular QnA
     */
    private String[] getQuestionsOrAnswers(String qnaUuid, String domainId, String clazzName) {
        List<String> ids = new ArrayList<String>();

        Config config = new Config(weaviateProtocol, weaviateHost, getHeaders());
        WeaviateClient client = new WeaviateClient(config);

        Field idField = Field.builder().name("id").build();
        Field[] subFields = new Field[1];
        subFields[0] = idField;

        Field additonalField = Field.builder().name("_additional").fields(subFields).build();
        Fields fields = Fields.builder().fields(new Field[]{ additonalField }).build();

        log.info("TODO: Search within knowledge base with domain Id: " + domainId);

        String[] path = {"qnaId"};
        WhereArgument whereArgument = WhereArgument.builder().
                operator(WhereOperator.Equal).
                valueString(qnaUuid).
                path(path).
                build();

        log.info("Get all objects '" + clazzName + "' linked with QnA '" + qnaUuid + "' ...");

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(clazzName)
                .withWhere(whereArgument)
                .withFields(fields)
                .run();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataNode = mapper.valueToTree(result.getResult().getData());
            log.info("Answers: " + dataNode);
            JsonNode questionNodes = dataNode.get("Get").get(clazzName);
            if (questionNodes.isArray()) {
                for (JsonNode question: questionNodes) {
                    JsonNode additionalNode = question.get("_additional");
                    log.info("_additional: " + additionalNode);
                    String id = additionalNode.get("id").asText();
                    ids.add(id);
                }
            }
        }

        return ids.toArray(new String[0]);
    }

    /**
     *
     */
    private ResponseEntity<String> createTenantWeaviateImpl(Domain domain) {
        log.info("Weaviate Impl: Create tenant associated with Katie domain ID '" + domain.getId() + "' ...");
        Config config = new Config(weaviateProtocol, weaviateHost, getHeaders());
        WeaviateClient client = new WeaviateClient(config);

        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("name", domain.getName());

        Result<WeaviateObject> result = client.data().creator()
                .withClassName(CLAZZ_TENANT)
                .withID(domain.getId())
                .withProperties(properties)
                .run();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            log.info("Create tenant result: " + result.getResult());
        }

        /*
        Result<Meta> meta = client.misc().metaGetter().run();
        if (meta.getError() == null) {
            log.info("meta.hostname: " + meta.getResult().getHostname());
            log.info("meta.version: " + meta.getResult().getVersion());
            log.info("meta.modules: " + meta.getResult().getModules());
        } else {
            log.error("" + meta.getError().getMessages());
        }
         */

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     *
     */
    private ResponseEntity<String> trainWeaviateImpl(QnA qna, String domainId) {
        log.info("Weaviate Impl: Index QnA associated with Katie domain ID '" + domainId + "' ...");

        index(domainId, qna.getUuid(), CLAZZ_QUESTION, FIELD_QUESTION, qna.getQuestion());
        index(domainId, qna.getUuid(), CLAZZ_ANSWER, FIELD_ANSWER, qna.getAnswer());

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @param key Key of field, either "question" or "answer"
     */
    private void index(String domainId, String uuid, String clazzName, String key, String value) {
        log.info("Weaviate Impl: Index '" + key + "' of QnA associated with Katie domain ID '" + domainId + "' ...");
        Config config = new Config(weaviateProtocol, weaviateHost, getHeaders());
        WeaviateClient client = new WeaviateClient(config);

        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("qnaId", uuid);
        properties.put(key, value); // TODO: escape and replace new lines
        properties.put(FIELD_TENANT, new ObjectReference[] {
                ObjectReference.builder().beacon("weaviate://localhost/" + domainId).build()
        });

        Result<WeaviateObject> result = client.data().creator()
                .withClassName(clazzName)
                .withID(UUID.randomUUID().toString())
                .withProperties(properties)
                .run();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            log.info("Index value result: " + result.getResult());
        }
    }

    /**
     * Get JWT from Authorization request header
     */
    private String getJWT(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            if (authorizationHeader.indexOf("Bearer") >= 0) {
                return authorizationHeader.substring("Bearer".length()).trim();
            } else {
                log.warn("Authorization header does not contain prefix 'Bearer'.");
                return null;
            }
        } else {
            log.warn("No Authorization header.");
            return null;
        }
    }
}

/**
 *
 */
class UuidCertainty {

    private String uuid;
    private double certainty;

    /**
     *
     */
    public UuidCertainty(String uuid, double certainty) {
        this.uuid = uuid;
        this.certainty = certainty;
    }

    /**
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     *
     */
    public double getCertainty() {
        return certainty;
    }

    /**
     *
     */
    public static Comparator<UuidCertainty> CertaintyComparator = new Comparator<UuidCertainty>() {

        @Override
        public int compare(UuidCertainty u1, UuidCertainty u2) {
            double c1 = u1.getCertainty();
            double c2 = u2.getCertainty();
            if (c1 > c2) {
                return -1;
            } else if (c1 == c2) {
                return 0;
            } else {
                return 1;
            }
        }
    };
}
