package com.flamingo.qa.api.graphql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphQlField {

    private final String name;
    private final Map<String, Object> arguments = new LinkedHashMap<>();
    private final List<GraphQlField> fields = new ArrayList<>();
    private final List<String> fragmentSpreads = new ArrayList<>();

    private GraphQlField(String name) {
        this.name = name;
    }

    public static GraphQlField field(String name) {
        return new GraphQlField(name);
    }

    public GraphQlField argument(String name, Object value) {
        arguments.put(name, value);
        return this;
    }

    public GraphQlField select(String... names) {
        for (String fieldName : names) {
            fields.add(field(fieldName));
        }
        return this;
    }

    public GraphQlField select(GraphQlField nestedField) {
        fields.add(nestedField);
        return this;
    }

    public GraphQlField spread(GraphQlFragment fragment) {
        fragmentSpreads.add(fragment.getName());
        return this;
    }

    String render() {
        StringBuilder builder = new StringBuilder(name);

        if (!arguments.isEmpty()) {
            builder.append("(").append(renderArguments(arguments)).append(")");
        }

        if (!fields.isEmpty() || !fragmentSpreads.isEmpty()) {
            builder.append(" { ").append(renderSelectionSet()).append(" }");
        }

        return builder.toString();
    }

    private String renderSelectionSet() {
        List<String> parts = new ArrayList<>();
        fields.forEach(field -> parts.add(field.render()));
        fragmentSpreads.forEach(fragment -> parts.add("..." + fragment));
        return String.join(" ", parts);
    }

    static String renderArguments(Map<String, Object> arguments) {
        return arguments.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + renderValue(entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    @SuppressWarnings("unchecked")
    static String renderValue(Object value) {
        if (value instanceof GraphQlVariable variable) {
            return "$" + variable.name();
        }
        if (value instanceof String string) {
            return "\"" + string + "\"";
        }
        if (value instanceof Map<?, ?> map) {
            return "{ " + renderArguments((Map<String, Object>) map) + " }";
        }
        return String.valueOf(value);
    }
}
