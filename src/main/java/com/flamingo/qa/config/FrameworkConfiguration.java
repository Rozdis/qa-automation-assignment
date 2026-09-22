package com.flamingo.qa.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@EnableConfigurationProperties({ApiProperties.class, GraphQlProperties.class, UiProperties.class})
@ComponentScan(basePackages = "com.flamingo.qa")
public class FrameworkConfiguration {
}
