package io.github.alexxfromgit.taf.mobile.core.driver;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.EnvironmentException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Hands out devices to test threads, one thread per device at a time.
 * <pre>
 * devices=emulator-5554,emulator-5556   # two devices -> run with thread-count="2"
 * </pre>
 * With no {@code devices} configured the pool has one anonymous slot (Appium picks the device), so parallel
 * threads queue up instead of fighting over the same device.
 */
public final class DevicePool {

    private static volatile DevicePool instance;

    private final BlockingQueue<DeviceLease> free;
    private final int size;

    public DevicePool(List<String> udids) {
        List<DeviceLease> leases = new ArrayList<>();
        if (udids.isEmpty()) {
            leases.add(new DeviceLease(0, null));
        } else {
            for (int i = 0; i < udids.size(); i++) {
                leases.add(new DeviceLease(i, udids.get(i)));
            }
        }
        this.free = new LinkedBlockingQueue<>(leases);
        this.size = leases.size();
    }

    public static DevicePool get() {
        DevicePool local = instance;
        if (local == null) {
            synchronized (DevicePool.class) {
                local = instance;
                if (local == null) {
                    local = new DevicePool(Arrays.stream(TafConfig.get().string("devices", "").split(","))
                            .map(String::trim).filter(s -> !s.isEmpty()).toList());
                    instance = local;
                }
            }
        }
        return local;
    }

    public DeviceLease lease(Duration timeout) {
        try {
            DeviceLease lease = free.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (lease == null) {
                throw new EnvironmentException("No free device within " + timeout + " (pool size " + size
                        + "). Add devices to 'devices' or lower the thread count.");
            }
            return lease;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EnvironmentException("Interrupted while waiting for a device", e);
        }
    }

    public void release(DeviceLease lease) {
        if (lease != null && !free.contains(lease)) {
            free.offer(lease);
        }
    }

    public int size() {
        return size;
    }

    public int available() {
        return free.size();
    }
}
