package org.wyona.webapp.controllers.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import org.wyona.webapp.models.katie.Domain;
import org.wyona.webapp.models.katie.QnA;
import org.wyona.webapp.models.katie.Sentence;

import technology.semi.weaviate.client.Config;
import technology.semi.weaviate.client.WeaviateClient;
import technology.semi.weaviate.client.v1.data.model.WeaviateObject;
import technology.semi.weaviate.client.base.Result;
import technology.semi.weaviate.client.v1.graphql.model.GraphQLResponse;
import technology.semi.weaviate.client.v1.graphql.query.argument.AskArgument;
import technology.semi.weaviate.client.v1.graphql.query.fields.Field;
import technology.semi.weaviate.client.v1.graphql.query.fields.Fields;
import technology.semi.weaviate.client.v1.misc.model.Meta;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 'Katie Mockup Connector' Controller
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v2")
@AllArgsConstructor
public class KatieMockupConnectorController implements KatieConnectorController {

    private static final String weaviateHost = "localhost:8080";
    private static final String weaviateProtocol = "http";

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
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#train(QnA)
     */
    @PostMapping("/qna")
    @ApiOperation(value = "Add QnA")
    public ResponseEntity<String> train(@RequestBody QnA qna) {
        return trainWeaviateImpl(qna);

        //log.info("TODO: Train QnA ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * @see org.wyona.webapp.controllers.v2.KatieConnectorController#getAnswers(Sentence)
     */
    @PostMapping("/ask")
    @ApiOperation(value = "Ask question")
    public ResponseEntity<String[]> getAnswers(@RequestBody Sentence question) {
        return getAnswersWeaviateImpl(question);

        //log.info("TODO: Get answers to question '" + question.getText() + "' ...");
        //return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     *
     */
    private ResponseEntity<String[]> getAnswersWeaviateImpl(Sentence question) {
        log.info("Weaviate Impl: Get answers to question '" + question.getText() + "' associated with Katie domain ID '" + "TODO" + "' ...");

        // TODO: Authentication: https://www.semi.technology/developers/weaviate/current/client-libraries/java.html#authentication
        Config config = new Config(weaviateProtocol, weaviateHost);
        WeaviateClient client = new WeaviateClient(config);

        Field questionField = Field.builder().name("question").build();
        Field uuidField = Field.builder().name("qnaId").build();
        Fields fields = Fields.builder().fields(new Field[]{ questionField, uuidField }).build();

        // TODO: Also use domain Id
        Float certaintyThreshold = Float.parseFloat("0.5");
        AskArgument askArgument = AskArgument.builder().question(question.getText()).certainty(certaintyThreshold).build();

        Result<GraphQLResponse> result = client.graphQL().get()
                .withClassName("Question")
                .withAsk(askArgument)
                .withFields(fields)
                .run();

        java.util.List<String> ids = new ArrayList<String>();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataNode = mapper.valueToTree(result.getResult().getData());
            log.info("Answers: " + dataNode);
            JsonNode questionNode = dataNode.get("Get").get("Question");
            if (questionNode.isArray()) {
                for (JsonNode answer: questionNode) {
                    ids.add(answer.get("qnaId").asText());
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
        Config config = new Config(weaviateProtocol, weaviateHost);
        WeaviateClient client = new WeaviateClient(config);

        Result<Boolean> result = client.data().deleter()
                .withID(domainId)
                .run();

        if (result.hasErrors()) {
            log.error("" + result.getError().getMessages());
        } else {
            log.info("Delete tenant result: " + result.getResult());
        }

        return new ResponseEntity<>(HttpStatus.OK);
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
                .withClassName("Tenant")
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
    private ResponseEntity<String> trainWeaviateImpl(QnA qna) {
        log.info("Weaviate Impl: Index QnA associated with Katie domain ID '" + qna.getDomainId() + "' ...");

        index(qna.getDomainId(), qna.getUuid(), "Question", "question", qna.getQuestion());
        index(qna.getDomainId(), qna.getUuid(), "Answer", "answer", qna.getAnswer());

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     *
     */
    private void index(String domainId, String uuid, String clazzName, String key, String value) {
        log.info("Weaviate Impl: Index '" + key + "' of QnA associated with Katie domain ID '" + domainId + "' ...");
        Config config = new Config(weaviateProtocol, weaviateHost);
        WeaviateClient client = new WeaviateClient(config);

        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("qnaId", uuid);
        properties.put(key, value); // TODO: escape and replace new lines
        properties.put("tenant", new java.util.HashMap() { {
            put("beacon", "weaviate://localhost/" + domainId);
        } });

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
