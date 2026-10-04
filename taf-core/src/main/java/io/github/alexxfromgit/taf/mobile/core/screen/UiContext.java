package io.github.alexxfromgit.taf.mobile.core.screen;

import io.github.alexxfromgit.taf.mobile.core.driver.DriverManager;
import io.github.alexxfromgit.taf.mobile.core.driver.Platform;
import org.openqa.selenium.SearchContext;

import java.util.function.Supplier;

/**
 * Where a screen or component searches for its elements: the driver for screens, the resolved root element
 * for components. Resolved lazily on every lookup, so elements never go stale between steps.
 */
public record UiContext(Supplier<? extends SearchContext> searchContext, Platform platform) {

    /** Context of the current thread's Appium session. */
    public static UiContext ofSession() {
        return new UiContext(DriverManager::driver, DriverManager.platform());
    }
}
