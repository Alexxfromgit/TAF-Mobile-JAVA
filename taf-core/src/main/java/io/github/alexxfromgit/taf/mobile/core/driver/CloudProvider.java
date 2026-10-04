package io.github.alexxfromgit.taf.mobile.core.driver;

import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;

import java.util.Locale;

/**
 * Device clouds supported out of the box. Each one expects its settings in a vendor capability block;
 * everything under {@code cloud.options.*} goes there, plus the credentials read from environment variables
 * {@code CLOUD_USERNAME} and {@code CLOUD_ACCESS_KEY}.
 */
public enum CloudProvider {
    BROWSERSTACK("bstack:options", "https://hub.browserstack.com/wd/hub", "userName", "accessKey"),
    SAUCELABS("sauce:options", "https://ondemand.us-west-1.saucelabs.com/wd/hub", "username", "accessKey"),
    LAMBDATEST("LT:Options", "https://mobile-hub.lambdatest.com/wd/hub", "username", "accessKey");

    private final String optionsKey;
    private final String defaultUrl;
    private final String userField;
    private final String keyField;

    CloudProvider(String optionsKey, String defaultUrl, String userField, String keyField) {
        this.optionsKey = optionsKey;
        this.defaultUrl = defaultUrl;
        this.userField = userField;
        this.keyField = keyField;
    }

    public static CloudProvider parse(String value) {
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new FrameworkException("cloud.provider must be browserstack, saucelabs or lambdatest, not '"
                    + value + "'");
        }
    }

    public String optionsKey() {
        return optionsKey;
    }

    public String defaultUrl() {
        return defaultUrl;
    }

    public String userField() {
        return userField;
    }

    public String keyField() {
        return keyField;
    }
}
