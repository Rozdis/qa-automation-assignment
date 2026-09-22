package com.flamingo.qa.api.base;

import com.flamingo.qa.api.client.GraphQlClient;
import com.flamingo.qa.base.SpringContext;
import com.flamingo.qa.config.GraphQlProperties;

public abstract class BaseGraphQlTest extends BaseRestAssuredTest {

    protected static final GraphQlProperties graphQlProperties = SpringContext.INSTANCE.getBean(GraphQlProperties.class);
    protected static final GraphQlClient graphQlClient = new GraphQlClient(graphQlProperties.getBaseUrl());
}
