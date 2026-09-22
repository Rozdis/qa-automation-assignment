package com.flamingo.qa.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ui")
@Getter
@Setter
public class UiProperties {

    private String baseUrl;
}
