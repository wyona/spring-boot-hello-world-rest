package org.wyona.webapp.controllers.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import org.wyona.webapp.models.katie.Domain;
import org.wyona.webapp.models.katie.QnA;
import org.wyona.webapp.models.katie.Sentence;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    private static final String CLAZZ_QUESTION = "Question";
    private static final String FIELD_QUESTION = "question";
    private static final String CLAZZ_ANSWER = "Answer";
    private static final String FIELD_ANSWER = "answer";
    private static final String CLAZZ_TENANT = "Tenant";
    private static final String FIELD_TENANT = "tenant";

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#createTenant(Domain)
     */
    @PostMapping("/tenant")
    @ApiOperation(value = "Create tenant")
    public ResponseEntity<String> createTenant(@RequestBody Domain domain) {
        return createTenantWeaviateImpl(domain);

        //log.info("TODO: Create tenant associated with Katie domain ID '" + domain.getId() + "' ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
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
        return deleteTenantWeaviateImpl(domainId);

        //log.info("TODO: Delete tenant associated with Katie domain ID '" + domainId + "' ...");
        //return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#train(QnA, String)
     */
    @PostMapping("/qna/{domain-id}")
    @ApiOperation(value = "Add QnA")
    public ResponseEntity<String> train(
            @RequestBody QnA qna,
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId
    ) {
        return trainWeaviateImpl(qna, domainId);

        //log.info("TODO: Train QnA ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#getAnswers(Sentence, String)
     */
    @PostMapping("/ask/{domain-id}")
    @ApiOperation(value = "Ask question")
    public ResponseEntity<String[]> getAnswers(
            @RequestBody Sentence question,
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId
    ) {
        return getAnswersWeaviateImpl(question, domainId);

        //log.info("TODO: Get answers to question '" + question.getText() + "' ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#deleteQnA(String, String)
     */
    @DeleteMapping("/qna/{domain-id}/{uuid}")
    @ApiOperation(value = "Delete QnA")
    public ResponseEntity<?> deleteQnA(
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId,
            @ApiParam(name = "uuid", value = "UUID of QnA", required = true)
            @PathVariable(name = "uuid", required = true) String uuid
    ) {
        return deleteQnAWeaviateImpl(domainId, uuid);

        //log.info("TODO: Delete QnA '" + uuid + "' ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     *
     */
    private ResponseEntity<String[]> getAnswersWeaviateImpl(Sentence question, String domainId) {
        log.info("Weaviate Impl: Get answers to question '" + question.getText() + "' associated with Katie domain ID '" + "TODO" + "' ...");

        // TODO: Authentication: https://www.semi.technology/developers/weaviate/current/client-libraries/java.html#authentication
        Config config = new Config(weaviateProtocol, weaviateHost);
        WeaviateClient client = new WeaviateClient(config);

        Field questionField = Field.builder().name(FIELD_QUESTION).build();
        Field uuidField = Field.builder().name("qnaId").build();
        Fields fields = Fields.builder().fields(new Field[]{ questionField, uuidField }).build();

        Float certaintyThreshold = Float.parseFloat("0.5");
        AskArgument askArgument = AskArgument.builder().question(question.getText()).certainty(certaintyThreshold).build();

        log.info("Search within knowledge base with domain Id: " + domainId);
        String[] path = {FIELD_TENANT, CLAZZ_TENANT, "id"};
        WhereArgument whereArgument = WhereArgument.builder().
                operator(WhereOperator.Equal).
                valueString(domainId).
                path(path).
                build();

        // {Get{Question(ask: {question:\"for dinner\",certainty: 0.5}, where: {operator:Equal, valueString:\"e4ff3246-372b-4042-a9e2-d30f612d1244\", path: [\"tenant\", \"Tenant\", \"id\"]}, limit: 10) {question qnaId _additional{certainty id answer {result}}},Answer(ask: {question:\"for dinner\",certainty: 0.5}, where: {operator:Equal, valueString:\"e4ff3246-372b-4042-a9e2-d30f612d1244\", path: [\"tenant\", \"Tenant\", \"id\"]}, limit: 10) {answer qnaId _additional{certainty id answer {result}}}}}

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName(CLAZZ_QUESTION)
                .withAsk(askArgument)
                .withWhere(whereArgument)
                .withFields(fields)
                .withLimit(10)
                .run();

        // TODO: Also search within schema class Answer

        java.util.List<String> ids = new ArrayList<String>();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataNode = mapper.valueToTree(result.getResult().getData());
            log.info("Answers: " + dataNode);
            JsonNode questionNodes = dataNode.get("Get").get(CLAZZ_QUESTION);
            if (questionNodes.isArray()) {
                for (JsonNode qNode: questionNodes) {
                    ids.add(qNode.get("qnaId").asText());
                }
            }
        }

        return new ResponseEntity<>(ids.toArray(new String[0]), HttpStatus.OK);
    }

    /**
     *
     */
    private ResponseEntity<String> deleteTenantWeaviateImpl(String domainId) {
        log.info("Weaviate Impl: Delete tenant associated with Katie domain ID '" + domainId + "' ...");

        if (deleteObject(domainId)) {
            log.info("Tenant '" + domainId + "' has been deleted successfully.");
        } else {
            log.error("Deleting tenant '" + domainId + "' failed!");
        }

        return new ResponseEntity<>(HttpStatus.OK);
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
        Config config = new Config(weaviateProtocol, weaviateHost);
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

        // TODO: Authentication: https://www.semi.technology/developers/weaviate/current/client-libraries/java.html#authentication
        Config config = new Config(weaviateProtocol, weaviateHost);
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
        Config config = new Config(weaviateProtocol, weaviateHost);
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
        Config config = new Config(weaviateProtocol, weaviateHost);
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
}
