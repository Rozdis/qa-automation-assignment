package com.flamingo.qa.api.client;

import com.flamingo.qa.api.model.graphql.GraphQlRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.restassured.RestAssured.given;

@RequiredArgsConstructor
public class GraphQlClient {

    private final String endpoint;

    public Response execute(String query) {
        return execute(query, null);
    }

    public Response execute(String query, Map<String, Object> variables) {
        GraphQlRequest request = GraphQlRequest.builder()
                .query(query)
                .variables(variables)
                .build();

        return given()
                .baseUri(endpoint)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post();
    }
}
