package com.flamingo.qa.api.base;

import com.flamingo.qa.base.AllureReporting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag("api")
public abstract class BaseRestAssuredTest {

    @BeforeAll
    static void setUp() {
        AllureReporting.registerRestAssuredFilter();
    }
}
