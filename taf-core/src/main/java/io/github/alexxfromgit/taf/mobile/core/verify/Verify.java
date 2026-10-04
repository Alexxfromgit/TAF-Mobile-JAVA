package io.github.alexxfromgit.taf.mobile.core.verify;

import io.github.alexxfromgit.taf.mobile.core.artifacts.FailureArtifacts;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.PotentialDefectException;
import io.github.alexxfromgit.taf.mobile.core.log.Log;
import io.github.alexxfromgit.taf.mobile.core.testng.TestContext;
import io.qameta.allure.model.Status;
import org.assertj.core.api.SoftAssertions;

import java.util.Objects;

/**
 * Business-level checks that read well in reports. Every check becomes an Allure step.
 * <p>
 * {@code verify.mode=hard} (default): the first failed check stops the test.<br>
 * {@code verify.mode=soft}: failures are collected and reported together when the test body ends,
 * so one run shows every broken expectation; each soft failure attaches a screenshot.
 * <p>
 * For rich AssertJ assertions use plain {@code assertThat(...)} (hard) or {@link #softly()} (always soft).
 */
public final class Verify {

    private Verify() {
    }

    public static void that(boolean condition, String description) {
        if (condition) {
            Log.step(description, Status.PASSED);
            return;
        }
        fail(description);
    }

    public static void equal(Object actual, Object expected, String description) {
        if (Objects.equals(actual, expected)) {
            Log.step(description + ": " + actual, Status.PASSED);
            return;
        }
        fail(description + ": expected [" + expected + "] but was [" + actual + "]");
    }

    /** Soft assertions bound to the current test; verified automatically after the test body. */
    public static SoftAssertions softly() {
        return TestContext.current().softAssertions();
    }

    public static boolean isSoftMode() {
        return "soft".equalsIgnoreCase(TafConfig.get().string("verify.mode", "hard"));
    }

    private static void fail(String message) {
        Log.step(message, Status.FAILED);
        if (isSoftMode()) {
            FailureArtifacts.screenshot("Screenshot: " + message);
            softly().fail(message);
        } else {
            throw new PotentialDefectException(message);
        }
    }
}
