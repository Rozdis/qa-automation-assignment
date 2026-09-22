package com.flamingo.qa.ui.base;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty()) {
            return;
        }

        context.getTestInstance()
                .filter(instance -> instance instanceof BaseUiTest)
                .map(instance -> ((BaseUiTest) instance).getPage())
                .ifPresent(page -> takeScreenshot(page, context.getDisplayName()));
    }

    private void takeScreenshot(Page page, String testName) {
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

        String fileName = testName.replaceAll("[^a-zA-Z0-9-_]", "_") + ".png";
        Path path = Path.of("target", "screenshots", fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, screenshot);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save failure screenshot", e);
        }

        Allure.addAttachment("Screenshot - " + testName, new ByteArrayInputStream(screenshot));
    }
}
