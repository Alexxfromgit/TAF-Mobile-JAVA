package io.github.alexxfromgit.taf.mobile.core.driver;

import io.appium.java_client.remote.options.BaseOptions;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OptionsBuilderTest {

    private static TafConfig config(Map<String, String> props, Map<String, String> env) {
        Map<String, String> all = new HashMap<>(props);
        return TafConfig.load(all, env, OptionsBuilderTest.class.getClassLoader());
    }

    @Test
    public void capabilitiesAreTypedAndPlatformSpecificOnesWin() {
        TafConfig config = config(Map.of(
                "caps.deviceName", "Any",
                "caps.noReset", "true",
                "caps.android.deviceName", "Pixel",
                "caps.android.adbExecTimeout", "60000",
                "caps.ios.deviceName", "iPhone 16"), Map.of());

        BaseOptions<?> android = OptionsBuilder.build(Platform.ANDROID, new DeviceLease(0, null), config, false);

        assertThat(android.getCapability("appium:deviceName")).isEqualTo("Pixel");
        assertThat(android.getCapability("appium:noReset")).isEqualTo(true);
        assertThat(android.getCapability("appium:adbExecTimeout")).isEqualTo(60000);
        assertThat(android.getCapability("appium:automationName").toString()).isEqualToIgnoringCase("UiAutomator2");
        assertThat(android.getPlatformName().toString()).isEqualToIgnoringCase("android");
    }

    @Test
    public void deviceLeaseGivesUniqueUdidAndPorts() {
        TafConfig config = config(Map.of(), Map.of());

        BaseOptions<?> second = OptionsBuilder.build(Platform.ANDROID, new DeviceLease(1, "emulator-5556"), config, false);
        BaseOptions<?> ios = OptionsBuilder.build(Platform.IOS, new DeviceLease(2, "SIM-UDID"), config, false);

        assertThat(second.getCapability("appium:udid")).isEqualTo("emulator-5556");
        assertThat(second.getCapability("appium:systemPort")).isEqualTo(8201);
        assertThat(ios.getCapability("appium:automationName").toString()).isEqualToIgnoringCase("XCUITest");
        assertThat(ios.getCapability("appium:wdaLocalPort")).isEqualTo(8102);
        assertThat(ios.getCapability("appium:mjpegServerPort")).isEqualTo(9102);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cloudCredentialsGoToTheVendorBlockFromEnvironment() {
        TafConfig config = config(Map.of(
                        "cloud.provider", "browserstack",
                        "cloud.app", "bs://abc123",
                        "cloud.options.deviceName", "Google Pixel 8",
                        "cloud.options.osVersion", "14.0",
                        "cloud.options.video", "true"),
                Map.of("CLOUD_USERNAME", "demo-user", "CLOUD_ACCESS_KEY", "demo-key"));

        BaseOptions<?> options = OptionsBuilder.build(Platform.ANDROID, new DeviceLease(0, "ignored"), config, true);

        Map<String, Object> vendor = (Map<String, Object>) options.getCapability("bstack:options");
        assertThat(vendor).containsEntry("deviceName", "Google Pixel 8")
                .containsEntry("osVersion", "14.0")
                .containsEntry("video", true)
                .containsEntry("userName", "demo-user")
                .containsEntry("accessKey", "demo-key");
        assertThat(options.getCapability("appium:app")).isEqualTo("bs://abc123");
        assertThat(options.getCapability("appium:udid")).as("cloud picks the device").isNull();
    }

    @Test
    public void eachProviderHasItsOwnBlock() {
        assertThat(CloudProvider.parse("saucelabs").optionsKey()).isEqualTo("sauce:options");
        assertThat(CloudProvider.parse("LambdaTest").optionsKey()).isEqualTo("LT:Options");
        assertThatThrownBy(() -> CloudProvider.parse("other")).isInstanceOf(FrameworkException.class);
    }

    @Test
    public void missingLocalAppExplainsHowToGetIt() {
        TafConfig config = config(Map.of("app.path", "apps/does-not-exist.apk"), Map.of());
        assertThatThrownBy(() -> OptionsBuilder.build(Platform.ANDROID, new DeviceLease(0, null), config, false))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("download-apps");
    }

    @Test
    public void typedValues() {
        assertThat(OptionsBuilder.typed("false")).isEqualTo(false);
        assertThat(OptionsBuilder.typed("42")).isEqualTo(42);
        assertThat(OptionsBuilder.typed("12345678901")).isEqualTo(12345678901L);
        assertThat(OptionsBuilder.typed("14.0")).isEqualTo("14.0");
    }
}
