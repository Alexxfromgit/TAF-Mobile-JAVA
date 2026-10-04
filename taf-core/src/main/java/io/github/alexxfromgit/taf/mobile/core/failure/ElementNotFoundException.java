package io.github.alexxfromgit.taf.mobile.core.failure;

/**
 * A UI element did not appear in time (Allure: failed, "UI element not found"). Either the app changed
 * (missing/renamed element) or a locator is outdated - the message names the screen field and its locator.
 */
public class ElementNotFoundException extends AssertionError {

    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
