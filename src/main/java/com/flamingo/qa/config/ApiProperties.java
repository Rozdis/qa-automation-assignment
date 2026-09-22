package com.flamingo.qa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "booker")
@Getter
@Setter
public class ApiProperties {

    private String baseUrl;
    private String username;
    private String password;
}
