package io.github.alexxfromgit.taf.mobile.core;

import io.appium.java_client.AppiumDriver;
import io.github.alexxfromgit.taf.mobile.core.artifacts.FailureArtifacts;
import io.github.alexxfromgit.taf.mobile.core.artifacts.ScreenRecorder;
import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.driver.DriverManager;
import io.github.alexxfromgit.taf.mobile.core.precondition.PreconditionRunner;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenFactory;
import io.github.alexxfromgit.taf.mobile.core.session.AppLifecycle;
import io.github.alexxfromgit.taf.mobile.core.session.FreshSession;
import io.github.alexxfromgit.taf.mobile.core.session.ResetAppData;
import io.github.alexxfromgit.taf.mobile.core.session.SessionPolicy;
import io.github.alexxfromgit.taf.mobile.core.session.StartMode;
import io.github.alexxfromgit.taf.mobile.core.users.TestUsers;
import io.github.alexxfromgit.taf.mobile.core.users.UserCredentials;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

/**
 * Base class for UI tests. Before each test it prepares the session with the cheapest safe
 * {@link StartMode} (see {@link SessionPolicy}), starts screen recording if enabled and runs the test's
 * preconditions. Sessions are closed at the end of the run.
 */
public abstract class MobileTestBase {

    private static final ThreadLocal<Boolean> PREVIOUS_FAILED = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<UserCredentials> USER = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void prepareSession(Method method) {
        Optional<String> nextUser = TestUsers.aliasOf(method);
        TafConfig config = TafConfig.get();
        SessionPolicy.Situation situation = new SessionPolicy.Situation(
                DriverManager.hasSession(),
                PREVIOUS_FAILED.get(),
                annotated(method, FreshSession.class),
                annotated(method, ResetAppData.class),
                DriverManager.user().orElse(null),
                nextUser.orElse(null));
        StartMode mode = SessionPolicy.decide(situation,
                mode(config.string("session.default")), mode(config.string("session.after-failure")));

        try {
            AppLifecycle.apply(mode);
            PREVIOUS_FAILED.set(false);
            DriverManager.user(nextUser.orElse(null));
            USER.set(nextUser.map(TestUsers::get).orElse(null));

            ScreenRecorder.start();
            PreconditionRunner.run(method, user());
        } catch (RuntimeException | Error e) {
            // attach evidence while the Allure fixture is still open, and force a clean start next time
            FailureArtifacts.onFailure();
            PREVIOUS_FAILED.set(true);
            throw e;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void finishTest(ITestResult result) {
        boolean failed = result.getStatus() == ITestResult.FAILURE;
        ScreenRecorder.stop(failed);
        PREVIOUS_FAILED.set(failed);
    }

    /** Credentials of this test's {@code @TestUser}, if any. */
    protected Optional<UserCredentials> user() {
        return Optional.ofNullable(USER.get());
    }

    /** The current screen of type {@code type}, after waiting until it is ready. */
    protected <S extends Screen> S on(Class<S> type) {
        return ScreenFactory.create(type).waitReady();
    }

    protected AppiumDriver driver() {
        return DriverManager.driver();
    }

    private static boolean annotated(Method method, Class<? extends Annotation> annotation) {
        return method.isAnnotationPresent(annotation) || method.getDeclaringClass().isAnnotationPresent(annotation);
    }

    private static StartMode mode(String value) {
        return StartMode.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
