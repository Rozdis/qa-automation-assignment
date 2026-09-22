package com.flamingo.qa.api.graphql;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphQlFragment {

    @Getter
    private final String name;
    private final String onType;
    private final List<GraphQlField> fields = new ArrayList<>();

    private GraphQlFragment(String name, String onType) {
        this.name = name;
        this.onType = onType;
    }

    public static GraphQlFragment on(String name, String onType) {
        return new GraphQlFragment(name, onType);
    }

    public GraphQlFragment select(String... names) {
        for (String fieldName : names) {
            fields.add(GraphQlField.field(fieldName));
        }
        return this;
    }

    public GraphQlFragment select(GraphQlField field) {
        fields.add(field);
        return this;
    }

    String render() {
        String selectionSet = fields.stream()
                .map(GraphQlField::render)
                .collect(Collectors.joining(" "));
        return "fragment " + name + " on " + onType + " { " + selectionSet + " }";
    }
}
