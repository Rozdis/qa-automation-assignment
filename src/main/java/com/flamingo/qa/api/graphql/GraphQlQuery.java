package com.flamingo.qa.api.graphql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQlQuery {

    private String operationName;
    private final Map<String, String> variableDefinitions = new LinkedHashMap<>();
    private final List<GraphQlFragment> fragments = new ArrayList<>();
    private GraphQlField rootField;

    public static GraphQlQuery query() {
        return new GraphQlQuery();
    }

    public static GraphQlVariable var(String name) {
        return new GraphQlVariable(name);
    }

    public GraphQlQuery name(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public GraphQlQuery variable(String name, String graphQlType) {
        variableDefinitions.put(name, graphQlType);
        return this;
    }

    public GraphQlQuery fragment(GraphQlFragment fragment) {
        fragments.add(fragment);
        return this;
    }

    public GraphQlQuery select(GraphQlField field) {
        this.rootField = field;
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();

        fragments.forEach(fragment -> builder.append(fragment.render()).append(" "));

        builder.append("query");
        if (operationName != null) {
            builder.append(" ").append(operationName);
        }
        if (!variableDefinitions.isEmpty()) {
            String vars = variableDefinitions.entrySet().stream()
                    .map(entry -> "$" + entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining(", "));
            builder.append("(").append(vars).append(")");
        }
        builder.append(" { ").append(rootField.render()).append(" }");

        return builder.toString();
    }
}
