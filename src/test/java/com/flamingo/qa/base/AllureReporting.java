package com.flamingo.qa.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

public final class AllureReporting {

    private static volatile boolean restAssuredFilterRegistered = false;

    public static synchronized void registerRestAssuredFilter() {
        if (!restAssuredFilterRegistered) {
            RestAssured.filters(new AllureRestAssured());
            restAssuredFilterRegistered = true;
        }
    }

    private AllureReporting() {
    }
}
