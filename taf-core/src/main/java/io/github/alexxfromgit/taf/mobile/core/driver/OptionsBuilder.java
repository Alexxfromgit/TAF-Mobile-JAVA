package io.github.alexxfromgit.taf.mobile.core.driver;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.options.BaseOptions;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds Appium capabilities from configuration:
 * <pre>
 * app.path=apps/my-demo-app.apk            # relative to the project, absolute, or http(s) URL
 * caps.deviceName=Android Emulator         # any capability, for every platform ("appium:" added automatically)
 * caps.android.avd=Pixel_8                 # platform-specific
 * caps.ios.platformVersion=18.4
 * cloud.app=bs://abc123                    # target=cloud: uploaded app id (overrides app.path)
 * cloud.options.deviceName=Google Pixel 8  # target=cloud: vendor block (bstack:options, sauce:options, LT:Options)
 * </pre>
 * Values {@code true/false} and integers are typed; everything else is a string.
 */
public final class OptionsBuilder {

    private OptionsBuilder() {
    }

    public static BaseOptions<?> build(Platform platform, DeviceLease lease, TafConfig config, boolean cloud) {
        BaseOptions<?> options = platform == Platform.ANDROID ? new UiAutomator2Options() : new XCUITestOptions();
        options.setCapability("newCommandTimeout", config.duration("appium.new-command-timeout").toSeconds());

        String app = cloud ? config.optional("cloud.app").or(() -> config.optional("app.path")).orElse(null)
                : config.optional("app.path").orElse(null);
        if (app != null) {
            options.setCapability("app", cloud ? app : resolveApp(app));
        }

        if (!cloud && lease != null) {
            if (lease.udid() != null) {
                options.setCapability("udid", lease.udid());
            }
            if (platform == Platform.ANDROID) {
                options.setCapability("systemPort", lease.port(config.integer("device.android.system-port-base")));
            } else {
                options.setCapability("wdaLocalPort", lease.port(config.integer("device.ios.wda-port-base")));
                options.setCapability("mjpegServerPort", lease.port(config.integer("device.ios.mjpeg-port-base")));
            }
        }

        capabilities(config, platform).forEach(options::setCapability);

        if (cloud) {
            CloudProvider provider = CloudProvider.parse(config.string("cloud.provider"));
            Map<String, Object> vendor = new LinkedHashMap<>();
            config.withPrefix("cloud.options.").forEach((k, v) -> vendor.put(k, typed(v)));
            vendor.put(provider.userField(), config.secret("cloud.username"));
            vendor.put(provider.keyField(), config.secret("cloud.access-key"));
            options.setCapability(provider.optionsKey(), vendor);
        }
        return options;
    }

    /** {@code caps.*} for every platform, then {@code caps.<platform>.*} (which wins). */
    static Map<String, Object> capabilities(TafConfig config, Platform platform) {
        Map<String, Object> caps = new LinkedHashMap<>();
        config.withPrefix("caps.").forEach((key, value) -> {
            if (!key.startsWith("android.") && !key.startsWith("ios.")) {
                caps.put(key, typed(value));
            }
        });
        config.withPrefix("caps." + platform.key() + ".").forEach((key, value) -> caps.put(key, typed(value)));
        return caps;
    }

    static Object typed(String value) {
        String v = value.trim();
        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(v);
        }
        if (v.matches("-?\\d{1,9}")) {
            return Integer.parseInt(v);
        }
        if (v.matches("-?\\d{10,18}")) {
            return Long.parseLong(v);
        }
        return v;
    }

    static String resolveApp(String app) {
        if (app.contains("://")) {
            return app;   // URL or cloud storage id
        }
        Path path = Path.of(app);
        if (!path.isAbsolute()) {
            path = Path.of(System.getProperty("taf.project-dir", ".")).resolve(path);
        }
        path = path.toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new FrameworkException("App not found: " + path + ". Download the demo apps with "
                    + "scripts/download-apps.sh (or .ps1), or point app.path to your build.");
        }
        return path.toString();
    }
}
