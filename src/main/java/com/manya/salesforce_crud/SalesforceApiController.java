
package com.manya.salesforce_crud;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/salesforce")
public class SalesforceApiController {

    @Value("${salesforce.instance-url}")
    private String instanceUrl;

    private static final String API_VERSION = "/services/data/v60.0";

    private static final String OBJECT_PATTERN =
            "Account|Contact|Lead|Opportunity|Case";

    private RestClient salesforceClient(OAuth2AuthorizedClient client) {
        return RestClient.builder()
                .baseUrl(instanceUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + client.getAccessToken().getTokenValue())
                .build();
    }

    private boolean isValidObject(String objectName) {
        return objectName != null
                && objectName.matches(OBJECT_PATTERN);
    }

    private boolean isValidId(String recordId) {
        return recordId != null
                && recordId.matches("[a-zA-Z0-9]{15,18}");
    }

    @GetMapping("/objects")
    public ResponseEntity<String> getObjects(
            @RegisteredOAuth2AuthorizedClient("salesforce")
            OAuth2AuthorizedClient client) {

        String response = salesforceClient(client)
                .get()
                .uri(API_VERSION + "/sobjects/")
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts")
    public ResponseEntity<String> getAccounts(
            @RegisteredOAuth2AuthorizedClient("salesforce")
            OAuth2AuthorizedClient client) {

        String response = salesforceClient(client)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_VERSION + "/query")
                        .queryParam(
                                "q",
                                "SELECT Id, Name, Type, Industry FROM Account LIMIT 20")
                        .build())
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/records/{objectName}")
    public ResponseEntity<String> getRecords(
            @PathVariable String objectName,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RegisteredOAuth2AuthorizedClient("salesforce")
            OAuth2AuthorizedClient client) {

        if (!isValidObject(objectName)) {
            return ResponseEntity.badRequest()
                    .body("Unsupported Salesforce object");
        }

        if (limit < 1 || limit > 200 || offset < 0) {
            return ResponseEntity.badRequest()
                    .body("Limit must be 1-200 and offset must be 0 or greater");
        }

        String query = "SELECT FIELDS(ALL) FROM "
                + objectName + " LIMIT " + limit
                + " OFFSET " + offset;

        String response = salesforceClient(client)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_VERSION + "/query")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/records/{objectName}")
    public ResponseEntity<String> createRecord(
            @PathVariable String objectName,
            @RequestBody String recordData,
            @RegisteredOAuth2AuthorizedClient("salesforce")
            OAuth2AuthorizedClient client) {

        if (!isValidObject(objectName)) {
            return ResponseEntity.badRequest()
                    .body("Unsupported Salesforce object");
        }

        return salesforceClient(client)
                .post()
                .uri(API_VERSION + "/sobjects/" + objectName + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(recordData)
                .exchange((request, response) -> {
                    String body = new String(
                            response.getBody().readAllBytes(),
                            StandardCharsets.UTF_8);

                    return ResponseEntity
                            .status(response.getStatusCode())
                            .body(body);
                });
    }

    @PatchMapping("/records/{objectName}/{recordId}")
    public ResponseEntity<String> updateRecord(
            @PathVariable String objectName,
            @PathVariable String recordId,
            @RequestBody String recordData,
            @RegisteredOAuth2AuthorizedClient("salesforce")
            OAuth2AuthorizedClient client) {

        if (!isValidObject(objectName)) {
            return ResponseEntity.badRequest()
                    .body("Unsupported Salesforce object");
        }

        if (!isValidId(recordId)) {
            return ResponseEntity.badRequest()
                    .body("Invalid Salesforce record ID");
        }

        return salesforceClient(client)
                .patch()
                .uri(API_VERSION + "/sobjects/"
                        + objectName + "/" + recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(recordData)
                .exchange((request, response) -> {
                    String body = new String(
                            response.getBody().readAllBytes(),
                            StandardCharsets.UTF_8);

                    if (body.isBlank()) {
                        return ResponseEntity
                                .status(response.getStatusCode())
                                .<String>build();
                    }

                    return ResponseEntity
                            .status(response.getStatusCode())
                            .body(body);
                });
    }

    @DeleteMapping("/records/{objectName}/{recordId}")
    public ResponseEntity<String> deleteRecord(
            @PathVariable String objectName,
            @PathVariable String recordId,
            @RegisteredOAuth2AuthorizedClient("salesforce")
            OAuth2AuthorizedClient client) {

        if (!isValidObject(objectName)) {
            return ResponseEntity.badRequest()
                    .body("Unsupported Salesforce object");
        }

        if (!isValidId(recordId)) {
            return ResponseEntity.badRequest()
                    .body("Invalid Salesforce record ID");
        }

        return salesforceClient(client)
                .delete()
                .uri(API_VERSION + "/sobjects/"
                        + objectName + "/" + recordId)
                .exchange((request, response) -> {
                    String body = new String(
                            response.getBody().readAllBytes(),
                            StandardCharsets.UTF_8);

                    if (body.isBlank()) {
                        return ResponseEntity
                                .status(response.getStatusCode())
                                .<String>build();
                    }

                    return ResponseEntity
                            .status(response.getStatusCode())
                            .body(body);
                });
    }
}
