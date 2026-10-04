package io.github.alexxfromgit.taf.mobile.core.testng;

import io.github.alexxfromgit.taf.mobile.core.artifacts.FailureArtifacts;
import io.github.alexxfromgit.taf.mobile.core.failure.KnownIssueException;
import io.github.alexxfromgit.taf.mobile.core.log.Log;
import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Link;
import io.qameta.allure.util.ResultsUtils;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;

/**
 * Per-invocation lifecycle:
 * <ol>
 *     <li>before: fresh {@link TestContext};</li>
 *     <li>after a passed test body: soft assertions are verified;</li>
 *     <li>after a failed test or fixture: screenshot and page source are attached;</li>
 *     <li>after every test: {@link KnownIssue} handling, severity from groups.</li>
 * </ol>
 * Registered through {@code META-INF/services/org.testng.ITestNGListener}.
 */
public class TafInvocationListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod invoked, ITestResult result) {
        TestContext.start(methodOf(invoked));
    }

    @Override
    public void afterInvocation(IInvokedMethod invoked, ITestResult result) {
        if (invoked.isTestMethod() && result.getStatus() == ITestResult.SUCCESS) {
            try {
                TestContext.current().softAssertionsIfUsed().ifPresent(soft -> soft.assertAll());
            } catch (Throwable t) {
                result.setStatus(ITestResult.FAILURE);
                result.setThrowable(t);
            }
        }
        if (result.getStatus() == ITestResult.FAILURE) {
            FailureArtifacts.onFailure();
        }
        if (invoked.isTestMethod()) {
            Method method = methodOf(invoked);
            handleKnownIssue(method, result);
            applySeverity(invoked, method);
        }
    }

    private static void handleKnownIssue(Method method, ITestResult result) {
        KnownIssue issue = method.isAnnotationPresent(KnownIssue.class)
                ? method.getAnnotation(KnownIssue.class)
                : method.getDeclaringClass().getAnnotation(KnownIssue.class);
        if (issue == null) {
            return;
        }
        Link link = issue.url().isBlank()
                ? ResultsUtils.createIssueLink(issue.id())
                : new Link().setName(issue.id()).setType("issue").setUrl(issue.url());
        updateTestCase(testCase -> {
            testCase.getLinks().add(link);
            testCase.setName("[KNOWN " + issue.id() + "] " + testCase.getName());
        });
        if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null
                && !(result.getThrowable() instanceof KnownIssueException)) {
            result.setThrowable(new KnownIssueException(issue.id(), result.getThrowable()));
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            Log.warn("Test passes although it is marked @KnownIssue(\"{}\") - is the bug fixed? Remove the annotation.",
                    issue.id());
        }
    }

    private static void applySeverity(IInvokedMethod invoked, Method method) {
        if (method.isAnnotationPresent(Severity.class) || method.getDeclaringClass().isAnnotationPresent(Severity.class)) {
            return;
        }
        Groups.severityOf(invoked.getTestMethod().getGroups()).ifPresent(level -> updateTestCase(testCase -> {
            testCase.getLabels().removeIf(l -> "severity".equals(l.getName()));
            testCase.getLabels().add(new Label().setName("severity").setValue(level.value()));
        }));
    }

    private static void updateTestCase(java.util.function.Consumer<io.qameta.allure.model.TestResult> update) {
        if (Allure.getLifecycle().getCurrentTestCase().isPresent()) {
            Allure.getLifecycle().updateTestCase(update::accept);
        }
    }

    private static Method methodOf(IInvokedMethod invoked) {
        return invoked.getTestMethod().getConstructorOrMethod().getMethod();
    }
}
