package io.github.alexxfromgit.taf.mobile.core.screen;

import io.appium.java_client.AppiumBy;
import io.github.alexxfromgit.taf.mobile.core.driver.Platform;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import org.openqa.selenium.By;

/** Turns a {@link Locate} into a Selenium {@link By} for the current platform. */
public final class LocatorResolver {

    private LocatorResolver() {
    }

    public static By resolve(Locate locate, Platform platform, String where) {
        Using specific = platform == Platform.ANDROID ? locate.android() : locate.ios();
        if (specific.how() != How.UNSET) {
            return toBy(specific.how(), specific.value(), platform, where);
        }
        if (!locate.value().isEmpty()) {
            return AppiumBy.accessibilityId(locate.value());
        }
        throw new FrameworkException(where + " has no locator for " + platform
                + ": set @Locate(value = ...) or @Locate(" + platform.key() + " = @Using(...))");
    }

    static By toBy(How how, String value, Platform platform, String where) {
        return switch (how) {
            case ACCESSIBILITY_ID -> AppiumBy.accessibilityId(value);
            case ID -> platform == Platform.ANDROID ? AppiumBy.id(value) : AppiumBy.name(value);
            case TEXT -> platform == Platform.ANDROID
                    ? AppiumBy.androidUIAutomator("new UiSelector().text(" + quote(value) + ")")
                    : AppiumBy.iOSNsPredicateString("label == " + quote(value) + " OR name == " + quote(value));
            case CLASS_NAME -> AppiumBy.className(value);
            case XPATH -> By.xpath(value);
            case ANDROID_UIAUTOMATOR -> onlyOn(Platform.ANDROID, platform, where, AppiumBy.androidUIAutomator(value));
            case IOS_CLASS_CHAIN -> onlyOn(Platform.IOS, platform, where, AppiumBy.iOSClassChain(value));
            case IOS_PREDICATE -> onlyOn(Platform.IOS, platform, where, AppiumBy.iOSNsPredicateString(value));
            case UNSET -> throw new FrameworkException(where + ": How.UNSET is not a locator strategy");
        };
    }

    private static By onlyOn(Platform expected, Platform actual, String where, By by) {
        if (expected != actual) {
            throw new FrameworkException(where + ": " + by + " only works on " + expected);
        }
        return by;
    }

    private static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
