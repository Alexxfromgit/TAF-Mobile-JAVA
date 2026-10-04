package io.github.alexxfromgit.taf.mobile.core.driver;

/**
 * Exclusive use of one device by one thread. The index gives every device its own set of ports
 * (UiAutomator2 systemPort, WebDriverAgent port, MJPEG port), so parallel sessions never collide.
 *
 * @param index position in the pool (0, 1, ...)
 * @param udid  device id, or {@code null} to let Appium pick the device
 */
public record DeviceLease(int index, String udid) {

    public int port(int base) {
        return base + index;
    }

    @Override
    public String toString() {
        return udid == null ? "device#" + index : udid;
    }
}
