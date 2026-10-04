package io.github.alexxfromgit.taf.mobile.core.log;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * Test-level logging. Each {@link #info} line goes to the log AND becomes an Allure step,
 * so the report reads like the test's narrative without sprinkling {@code @Step} everywhere.
 * Uses SLF4J {@code {}} placeholders.
 */
public final class Log {

    private static final Logger LOG = LoggerFactory.getLogger("taf");

    private Log() {
    }

    public static void info(String format, Object... args) {
        String message = format(format, args);
        LOG.info(message);
        step(message, Status.PASSED);
    }

    public static void warn(String format, Object... args) {
        String message = format(format, args);
        LOG.warn(message);
        step("WARNING: " + message, Status.BROKEN);
    }

    /** Log only, no Allure step. */
    public static void debug(String format, Object... args) {
        LOG.debug(format, args);
    }

    /** Adds a step with the given status when a test (or fixture) is currently running in Allure. */
    public static void step(String name, Status status) {
        if (Allure.getLifecycle().getCurrentTestCaseOrStep().isPresent()) {
            Allure.step(name, status);
        }
    }

    private static String format(String format, Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }
}
