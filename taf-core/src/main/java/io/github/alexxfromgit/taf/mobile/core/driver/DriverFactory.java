package io.github.alexxfromgit.taf.mobile.core.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.options.BaseOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.EnvironmentException;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates Appium sessions for the configured {@code target}:
 * <ul>
 *     <li>{@code local} - an Appium server you started yourself ({@code appium.url});</li>
 *     <li>{@code managed} - one Appium server per device, started and stopped by the framework
 *     (requires {@code appium} on the PATH);</li>
 *     <li>{@code cloud} - BrowserStack, Sauce Labs or LambdaTest ({@code cloud.provider}).</li>
 * </ul>
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);
    private static final Map<Integer, AppiumDriverLocalService> MANAGED = new ConcurrentHashMap<>();

    private DriverFactory() {
    }

    public static AppiumDriver create(Platform platform, DeviceLease lease) {
        TafConfig config = TafConfig.get();
        String target = config.string("target", "local").toLowerCase(Locale.ROOT);
        boolean cloud = target.equals("cloud");
        BaseOptions<?> options = OptionsBuilder.build(platform, lease, config, cloud);
        URL server = switch (target) {
            case "local" -> url(config.string("appium.url"));
            case "managed" -> managedServer(lease, config).getUrl();
            case "cloud" -> url(config.string("cloud.url",
                    CloudProvider.parse(config.string("cloud.provider")).defaultUrl()));
            default -> throw new FrameworkException("target must be local, managed or cloud, not '" + target + "'");
        };
        LOG.info("Starting {} session on {} ({})", platform, cloud ? config.string("cloud.provider") : server, lease);
        try {
            return platform == Platform.ANDROID ? new AndroidDriver(server, options) : new IOSDriver(server, options);
        } catch (WebDriverException e) {
            throw new EnvironmentException("Could not start an Appium session on " + (cloud ? "the cloud" : server)
                    + " for " + lease + ". Is the Appium server running, the driver installed and the device "
                    + "available? Cause: " + firstLine(e.getMessage()), e);
        }
    }

    /** Stops every Appium server started by the {@code managed} target. */
    public static void stopManagedServers() {
        MANAGED.values().forEach(service -> {
            try {
                service.stop();
            } catch (RuntimeException e) {
                LOG.warn("Could not stop Appium server {}", service.getUrl(), e);
            }
        });
        MANAGED.clear();
    }

    private static AppiumDriverLocalService managedServer(DeviceLease lease, TafConfig config) {
        return MANAGED.computeIfAbsent(lease.index(), index -> {
            AppiumServiceBuilder builder = new AppiumServiceBuilder().usingAnyFreePort();
            Arrays.stream(config.string("appium.args", "").split("\\s+"))
                    .filter(s -> !s.isBlank())
                    .forEach(arg -> builder.withArgument(() -> arg));
            AppiumDriverLocalService service = AppiumDriverLocalService.buildService(builder);
            try {
                service.start();
            } catch (RuntimeException e) {
                throw new EnvironmentException("Could not start a local Appium server. Install it with "
                        + "'npm install -g appium' and the driver with 'appium driver install uiautomator2'.", e);
            }
            LOG.info("Started managed Appium server {} for device #{}", service.getUrl(), index);
            return service;
        });
    }

    private static URL url(String value) {
        try {
            return URI.create(value).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new FrameworkException("Invalid Appium URL '" + value + "'", e);
        }
    }

    private static String firstLine(String message) {
        if (message == null) {
            return "";
        }
        int newline = message.indexOf('\n');
        return newline < 0 ? message : message.substring(0, newline);
    }
}
