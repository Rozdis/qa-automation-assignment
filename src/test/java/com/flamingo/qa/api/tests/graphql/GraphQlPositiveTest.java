package com.flamingo.qa.api.tests.graphql;

import com.flamingo.qa.api.base.BaseGraphQlTest;
import com.flamingo.qa.api.graphql.GraphQlFragment;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static com.flamingo.qa.api.graphql.GraphQlField.field;
import static com.flamingo.qa.api.graphql.GraphQlQuery.query;
import static com.flamingo.qa.api.graphql.GraphQlQuery.var;
import static org.assertj.core.api.Assertions.assertThat;

class GraphQlPositiveTest extends BaseGraphQlTest {

    @ParameterizedTest(name = "List products with first={0}")
    @ValueSource(ints = {1, 3, 5})
    @DisplayName("List products with pagination")
    void shouldListProductsWithPagination(int first) {
        String query = query()
                .select(field("products")
                        .argument("first", first)
                        .select("id", "name"))
                .build();

        Response response = graphQlClient.execute(query);

        assertThat(response.statusCode()).isEqualTo(200);

        List<String> ids = response.jsonPath().getList("data.products.id", String.class);
        List<String> names = response.jsonPath().getList("data.products.name", String.class);

        assertThat(ids).hasSize(first).allSatisfy(id -> assertThat(id).isNotBlank());
        assertThat(names).hasSize(first).allSatisfy(name -> assertThat(name).isNotBlank());
    }

    @Test
    @DisplayName("Get a single product by id")
    void shouldGetSingleProductById() {
        String productId = firstProductId();

        String query = query()
                .select(field("product")
                        .argument("where", Map.of("id", productId))
                        .select("id", "name"))
                .build();

        Response response = graphQlClient.execute(query);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("data.product.id")).isEqualTo(productId);
        assertThat(response.jsonPath().getString("data.product.name")).isNotBlank();
    }

    @Test
    @DisplayName("Query a single product using GraphQL variables")
    void shouldQueryProductUsingVariables() {
        String productId = firstProductId();

        String query = query()
                .name("GetProduct")
                .variable("id", "ID!")
                .select(field("product")
                        .argument("where", Map.of("id", var("id")))
                        .select("id", "name"))
                .build();

        Response response = graphQlClient.execute(query, Map.of("id", productId));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("data.product.id")).isEqualTo(productId);
        assertThat(response.jsonPath().getString("data.product.name")).isNotBlank();
    }

    @Test
    @DisplayName("Query nested fields across types using a fragment")
    void shouldQueryNestedFieldsUsingFragment() {
        String productId = firstProductId();

        GraphQlFragment productSummary = GraphQlFragment.on("ProductSummary", "Product")
                .select("id", "name")
                .select(field("categories").select("name"));

        String query = query()
                .fragment(productSummary)
                .select(field("product")
                        .argument("where", Map.of("id", productId))
                        .spread(productSummary))
                .build();

        Response response = graphQlClient.execute(query);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("data.product.id")).isEqualTo(productId);
        assertThat(response.jsonPath().getString("data.product.name")).isNotBlank();

        List<String> categoryNames = response.jsonPath().getList("data.product.categories.name", String.class);
        assertThat(categoryNames).isNotEmpty().allSatisfy(name -> assertThat(name).isNotBlank());
    }

    private String firstProductId() {
        String query = query()
                .select(field("products")
                        .argument("first", 1)
                        .select("id"))
                .build();

        Response response = graphQlClient.execute(query);
        return response.jsonPath().getString("data.products[0].id");
    }
}
