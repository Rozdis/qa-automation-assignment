package com.flamingo.qa.base;

import com.flamingo.qa.config.FrameworkConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class SpringContext {

    public static final ApplicationContext INSTANCE = new AnnotationConfigApplicationContext(FrameworkConfiguration.class);

    private SpringContext() {
    }
}
