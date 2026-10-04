package io.github.alexxfromgit.taf.mobile.core.failure;

/**
 * A screen did not become ready (identifiers visible, spinners gone) within its timeout
 * (Allure: failed, "UI element not found"). Usually a navigation problem or a very slow backend.
 */
public class ScreenNotReadyException extends AssertionError {

    public ScreenNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }
}
