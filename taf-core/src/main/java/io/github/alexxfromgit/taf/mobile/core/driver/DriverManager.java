package io.github.alexxfromgit.taf.mobile.core.driver;

import io.appium.java_client.AppiumDriver;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * One Appium session per thread. Sessions survive between tests (see {@code SessionPolicy}) and are closed
 * at the end of the run. A session always holds its {@link DeviceLease} so two threads never share a device.
 */
public final class DriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(DriverManager.class);

    /** A running session. */
    public record Session(AppiumDriver driver, Platform platform, DeviceLease lease, Instant startedAt) {
    }

    private static final ThreadLocal<Session> CURRENT = new ThreadLocal<>();
    private static final ThreadLocal<String> USER = new ThreadLocal<>();
    private static final Set<Session> ALL = ConcurrentHashMap.newKeySet();

    private DriverManager() {
    }

    /** Starts a new session for this thread (closing any previous one). */
    public static Session start() {
        quit();
        Platform platform = Platform.current();
        DevicePool pool = DevicePool.get();
        DeviceLease lease = pool.lease(TafConfig.get().duration("device.lease-timeout"));
        try {
            AppiumDriver driver = DriverFactory.create(platform, lease);
            driver.manage().timeouts().implicitlyWait(Duration.ZERO);   // explicit waits only
            Session session = new Session(driver, platform, lease, Instant.now());
            CURRENT.set(session);
            ALL.add(session);
            return session;
        } catch (RuntimeException e) {
            pool.release(lease);
            throw e;
        }
    }

    public static AppiumDriver driver() {
        return session().driver();
    }

    public static Session session() {
        Session session = CURRENT.get();
        if (session == null) {
            throw new FrameworkException("No mobile session on this thread. Extend MobileTestBase (or call "
                    + "DriverManager.start()) before using screens.");
        }
        return session;
    }

    public static Optional<Session> currentSession() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static boolean hasSession() {
        return CURRENT.get() != null;
    }

    /** Platform of the running session, or the configured platform when no session exists yet. */
    public static Platform platform() {
        Session session = CURRENT.get();
        return session != null ? session.platform() : Platform.current();
    }

    /** Alias of the user the app is currently logged in as on this thread (managed by MobileTestBase). */
    public static Optional<String> user() {
        return Optional.ofNullable(USER.get());
    }

    public static void user(String alias) {
        if (alias == null) {
            USER.remove();
        } else {
            USER.set(alias);
        }
    }

    /** Closes this thread's session and releases its device. */
    public static void quit() {
        Session session = CURRENT.get();
        CURRENT.remove();
        USER.remove();
        if (session != null) {
            close(session);
        }
    }

    /** Closes every session of every thread (end of run). */
    public static void quitAll() {
        CURRENT.remove();
        USER.remove();
        ALL.forEach(DriverManager::close);
    }

    private static void close(Session session) {
        if (!ALL.remove(session)) {
            return;
        }
        try {
            session.driver().quit();
        } catch (RuntimeException e) {
            LOG.warn("Could not quit session on {}: {}", session.lease(), e.getMessage());
        } finally {
            DevicePool.get().release(session.lease());
        }
    }
}
