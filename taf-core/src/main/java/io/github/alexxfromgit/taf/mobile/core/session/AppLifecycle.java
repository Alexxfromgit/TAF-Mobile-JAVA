package io.github.alexxfromgit.taf.mobile.core.session;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.driver.DriverManager;
import io.github.alexxfromgit.taf.mobile.core.driver.Platform;
import io.github.alexxfromgit.taf.mobile.core.log.Log;

import java.util.Map;
import java.util.Optional;

/**
 * Applies a {@link StartMode} to the current thread's session. Needs {@code app.id.android} / {@code app.id.ios}
 * (package name / bundle id) for restart and reset; without it, a new session is started instead.
 */
public final class AppLifecycle {

    private AppLifecycle() {
    }

    public static void apply(StartMode mode) {
        Optional<String> appId = TafConfig.get().optional("app.id." + DriverManager.platform().key());
        StartMode effective = (mode == StartMode.RESTART_APP || mode == StartMode.RESET_APP_DATA)
                && (appId.isEmpty() || !DriverManager.hasSession()) ? StartMode.NEW_SESSION : mode;
        Log.info("Start mode: {}", effective);
        switch (effective) {
            case NEW_SESSION -> DriverManager.start();
            case RESTART_APP -> restart(appId.orElseThrow());
            case RESET_APP_DATA -> reset(appId.orElseThrow());
            case REUSE -> { }
        }
    }

    private static void restart(String appId) {
        InteractsWithApps apps = (InteractsWithApps) DriverManager.driver();
        apps.terminateApp(appId);
        apps.activateApp(appId);
    }

    private static void reset(String appId) {
        AppiumDriver driver = DriverManager.driver();
        InteractsWithApps apps = (InteractsWithApps) driver;
        apps.terminateApp(appId);
        if (DriverManager.platform() == Platform.ANDROID) {
            driver.executeScript("mobile: clearApp", Map.of("appId", appId));
        } else {
            // iOS has no "clear data": reinstall the app (slower; prefer RESTART_APP where possible)
            String app = String.valueOf(driver.getCapabilities().getCapability("appium:app"));
            apps.removeApp(appId);
            apps.installApp(app);
        }
        apps.activateApp(appId);
    }
}
