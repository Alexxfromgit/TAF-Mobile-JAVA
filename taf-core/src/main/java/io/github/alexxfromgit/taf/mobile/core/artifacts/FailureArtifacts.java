package io.github.alexxfromgit.taf.mobile.core.artifacts;

import io.appium.java_client.AppiumDriver;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.driver.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Evidence for failures: a screenshot and the page source (the UI hierarchy XML - the fastest way to fix a
 * broken locator). Captured automatically for failed tests and failed fixtures; never fails the test itself.
 */
public final class FailureArtifacts {

    private static final Logger LOG = LoggerFactory.getLogger(FailureArtifacts.class);

    private FailureArtifacts() {
    }

    public static void onFailure() {
        if (!DriverManager.hasSession() || Allure.getLifecycle().getCurrentTestCaseOrStep().isEmpty()) {
            return;
        }
        TafConfig config = TafConfig.get();
        if (config.bool("artifacts.screenshot-on-failure", true)) {
            screenshot("Screenshot on failure");
        }
        if (config.bool("artifacts.page-source-on-failure", true)) {
            pageSource("Page source on failure");
        }
    }

    public static void screenshot(String name) {
        try {
            AppiumDriver driver = DriverManager.driver();
            byte[] png = driver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), ".png");
        } catch (RuntimeException e) {
            LOG.warn("Could not take a screenshot: {}", e.getMessage());
        }
    }

    public static void pageSource(String name) {
        try {
            String xml = DriverManager.driver().getPageSource();
            Allure.addAttachment(name, "text/xml",
                    new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), ".xml");
        } catch (RuntimeException e) {
            LOG.warn("Could not read the page source: {}", e.getMessage());
        }
    }
}
