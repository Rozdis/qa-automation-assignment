package com.flamingo.qa.api.tests.graphql;

import com.flamingo.qa.api.base.BaseGraphQlTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.flamingo.qa.api.graphql.GraphQlField.field;
import static com.flamingo.qa.api.graphql.GraphQlQuery.query;
import static org.assertj.core.api.Assertions.assertThat;

class GraphQlNegativeTest extends BaseGraphQlTest {

    @Test
    @DisplayName("Querying a non-existent id returns HTTP 200 with null data and no errors")
    void shouldReturnNullDataForNonExistentId() {
        String query = query()
                .select(field("product")
                        .argument("where", Map.of("id", "does-not-exist-123"))
                        .select("id", "name"))
                .build();

        Response response = graphQlClient.execute(query);
        Object productData = response.jsonPath().get("data.product");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(productData).isNull();
        assertThat(response.jsonPath().getList("errors")).isNull();
    }

    @Test
    @DisplayName("Malformed query syntax returns an error and no data")
    void shouldReturnErrorForMalformedQuery() {
        String malformedQuery = "query { products(first: 2) { id name ";

        Response response = graphQlClient.execute(malformedQuery);
        Object data = response.jsonPath().get("data");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("errors[0].message")).containsIgnoringCase("parse error");
        assertThat(data).isNull();
    }

    @Test
    @DisplayName("Requesting a non-existent field returns a validation error")
    void shouldReturnValidationErrorForNonExistentField() {
        String query = query()
                .select(field("products")
                        .argument("first", 1)
                        .select("id", "nonExistentField"))
                .build();

        Response response = graphQlClient.execute(query);
        Object data = response.jsonPath().get("data");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("errors[0].message"))
                .contains("nonExistentField")
                .containsIgnoringCase("not defined");
        assertThat(data).isNull();
    }
}
