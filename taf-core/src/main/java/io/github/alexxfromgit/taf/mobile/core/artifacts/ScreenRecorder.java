package io.github.alexxfromgit.taf.mobile.core.artifacts;

import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.driver.DriverManager;
import io.github.alexxfromgit.taf.mobile.core.driver.Platform;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;

/**
 * Screen recording through Appium (Android and iOS, also on device clouds).
 * {@code artifacts.video=off | on-failure | always}.
 */
public final class ScreenRecorder {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenRecorder.class);
    private static final ThreadLocal<Boolean> RECORDING = ThreadLocal.withInitial(() -> false);

    private ScreenRecorder() {
    }

    private static String mode() {
        return TafConfig.get().string("artifacts.video", "off").toLowerCase(Locale.ROOT);
    }

    public static void start() {
        if (mode().equals("off") || !DriverManager.hasSession()) {
            return;
        }
        try {
            Duration limit = TafConfig.get().duration("artifacts.video.max-duration");
            CanRecordScreen recorder = (CanRecordScreen) DriverManager.driver();
            if (DriverManager.platform() == Platform.ANDROID) {
                recorder.startRecordingScreen(new AndroidStartScreenRecordingOptions().withTimeLimit(limit));
            } else {
                recorder.startRecordingScreen(new IOSStartScreenRecordingOptions().withTimeLimit(limit));
            }
            RECORDING.set(true);
        } catch (RuntimeException e) {
            LOG.warn("Could not start screen recording: {}", e.getMessage());
        }
    }

    /** Stops recording and attaches the video when the test failed (or always, depending on the mode). */
    public static void stop(boolean failed) {
        if (!RECORDING.get() || !DriverManager.hasSession()) {
            RECORDING.set(false);
            return;
        }
        RECORDING.set(false);
        try {
            String base64 = ((CanRecordScreen) DriverManager.driver()).stopRecordingScreen();
            if (failed || mode().equals("always")) {
                Allure.addAttachment("Screen recording", "video/mp4",
                        new ByteArrayInputStream(Base64.getMimeDecoder().decode(base64)), ".mp4");
            }
        } catch (RuntimeException e) {
            LOG.warn("Could not stop screen recording: {}", e.getMessage());
        }
    }
}
