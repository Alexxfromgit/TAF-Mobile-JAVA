package io.github.alexxfromgit.taf.mobile.core.driver;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;

import java.util.Locale;

/** Mobile platform under test, selected with {@code platform=android|ios}. */
public enum Platform {
    ANDROID, IOS;

    public static Platform current() {
        return parse(TafConfig.get().string("platform", "android"));
    }

    public static Platform parse(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "android" -> ANDROID;
            case "ios" -> IOS;
            default -> throw new FrameworkException("platform must be android or ios, not '" + value + "'");
        };
    }

    /** Lower-case name used in configuration keys, e.g. {@code app.id.android}. */
    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }
}
