package com.flamingo.qa.ui.base;

import com.flamingo.qa.config.FrameworkConfiguration;
import com.flamingo.qa.config.UiProperties;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Tag("ui")
@ExtendWith(ScreenshotOnFailureExtension.class)
@SpringJUnitConfig(FrameworkConfiguration.class)
public abstract class BaseUiTest {

    @Autowired
    protected UiProperties uiProperties;

    private static Playwright playwright;
    private static Browser browser;

    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void createPage() {
        page = browser.newPage();
    }

    @AfterEach
    void closePage() {
        page.close();
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    protected Page getPage() {
        return page;
    }
}
