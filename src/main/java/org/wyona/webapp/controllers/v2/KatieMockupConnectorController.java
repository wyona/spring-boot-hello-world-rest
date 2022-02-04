package org.wyona.webapp.controllers.v2;

import io.swagger.annotations.*;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.wyona.webapp.models.LanguageEmail;

import lombok.extern.slf4j.Slf4j;
import org.wyona.webapp.models.katie.Domain;

/**
 * 'Katie Mockup Connector' Controller
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v2")
@AllArgsConstructor
public class KatieMockupConnectorController {

    /**
     * Create tenant
     */
    @PostMapping("/tenant")
    @ApiOperation(value = "Create tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, response = String.class, message = "Tenant Id")
    })
    ResponseEntity<String> createTenant(@RequestBody Domain domain
            ){
        log.info("TODO: Create tenant associated with Katie domain ID '" + domain.getId() + "' ...");
        return ResponseEntity.ok("TODO");
    }

    /**
     * Delete tenant
     */
    @DeleteMapping("/tenant/{domain-id}")
    @ApiOperation(value = "Delete tenant")
    ResponseEntity<?> deleteTenant(
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId
    ){
        log.info("TODO: Delete tenant associated with Katie domain ID '" + domainId + "' ...");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
