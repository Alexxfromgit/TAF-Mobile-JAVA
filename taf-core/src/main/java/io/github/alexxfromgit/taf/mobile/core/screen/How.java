package io.github.alexxfromgit.taf.mobile.core.screen;

/** Locator strategies for {@link Using}. Prefer accessibility ids: stable, fast and good for real users too. */
public enum How {
    /** Not set: fall back to {@link Locate#value()}. */
    UNSET,
    /** Android content-description / iOS accessibilityIdentifier. Works on both platforms. */
    ACCESSIBILITY_ID,
    /** Android resource-id ({@code loginBtn} or {@code com.example:id/loginBtn}); iOS: name. */
    ID,
    /** Visible text: UiSelector().text() on Android, label/name predicate on iOS. */
    TEXT,
    CLASS_NAME,
    XPATH,
    ANDROID_UIAUTOMATOR,
    IOS_CLASS_CHAIN,
    IOS_PREDICATE
}
