package com.flamingo.qa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "graphql")
@Getter
@Setter
public class GraphQlProperties {

    private String baseUrl;
}
