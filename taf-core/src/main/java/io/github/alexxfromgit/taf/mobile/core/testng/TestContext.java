package io.github.alexxfromgit.taf.mobile.core.testng;

import org.assertj.core.api.SoftAssertions;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Per-invocation state (one test or configuration method on one thread). Reset by
 * {@link TafInvocationListener} before every invocation, which keeps the framework safe for parallel runs.
 */
public final class TestContext {

    private static final ThreadLocal<TestContext> CURRENT = ThreadLocal.withInitial(() -> new TestContext(null));

    private final Method method;
    private SoftAssertions softAssertions;

    private TestContext(Method method) {
        this.method = method;
    }

    public static TestContext current() {
        return CURRENT.get();
    }

    static TestContext start(Method method) {
        TestContext context = new TestContext(method);
        CURRENT.set(context);
        return context;
    }

    /** The running test method, if any (empty outside TestNG invocations). */
    public Optional<Method> method() {
        return Optional.ofNullable(method);
    }

    /** Soft assertions collected during this invocation; verified after the test body finishes. */
    public SoftAssertions softAssertions() {
        if (softAssertions == null) {
            softAssertions = new SoftAssertions();
        }
        return softAssertions;
    }

    Optional<SoftAssertions> softAssertionsIfUsed() {
        return Optional.ofNullable(softAssertions);
    }
}
