package io.github.alexxfromgit.taf.mobile.core.driver;

import io.github.alexxfromgit.taf.mobile.core.failure.EnvironmentException;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DevicePoolTest {

    @Test
    public void neverHandsOneDeviceToTwoThreads() throws InterruptedException {
        DevicePool pool = new DevicePool(List.of("emulator-5554", "emulator-5556"));
        Set<String> inUse = ConcurrentHashMap.newKeySet();
        AtomicInteger collisions = new AtomicInteger();
        AtomicInteger maxConcurrent = new AtomicInteger();
        ExecutorService threads = Executors.newFixedThreadPool(6);
        CountDownLatch done = new CountDownLatch(30);

        for (int i = 0; i < 30; i++) {
            threads.submit(() -> {
                DeviceLease lease = pool.lease(Duration.ofSeconds(5));
                if (!inUse.add(lease.udid())) {
                    collisions.incrementAndGet();
                }
                maxConcurrent.accumulateAndGet(inUse.size(), Math::max);
                sleep();
                inUse.remove(lease.udid());
                pool.release(lease);
                done.countDown();
            });
        }
        assertThat(done.await(20, TimeUnit.SECONDS)).isTrue();
        threads.shutdownNow();

        assertThat(collisions.get()).isZero();
        assertThat(maxConcurrent.get()).isLessThanOrEqualTo(2);
        assertThat(pool.available()).isEqualTo(2);
    }

    @Test
    public void emptyConfigurationMeansOneAnonymousDevice() {
        DevicePool pool = new DevicePool(List.of());
        DeviceLease lease = pool.lease(Duration.ofMillis(100));

        assertThat(lease.udid()).isNull();
        assertThatThrownBy(() -> pool.lease(Duration.ofMillis(50)))
                .isInstanceOf(EnvironmentException.class)
                .hasMessageContaining("No free device");
        pool.release(lease);
        assertThat(pool.lease(Duration.ofMillis(50))).isEqualTo(lease);
    }

    @Test
    public void portsAreDerivedFromTheIndex() {
        assertThat(new DeviceLease(3, "x").port(8200)).isEqualTo(8203);
    }

    private static void sleep() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
