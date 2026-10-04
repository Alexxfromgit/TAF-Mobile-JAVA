package io.github.alexxfromgit.taf.mobile.core.testng;

import io.qameta.allure.SeverityLevel;

import java.util.Map;
import java.util.Optional;

/**
 * Shared TestNG group names. Priority groups are mapped to Allure severity automatically
 * (unless the test has an explicit {@code @Severity}).
 */
public final class Groups {

    public static final String SMOKE = "smoke";
    public static final String REGRESSION = "regression";
    public static final String E2E = "e2e";

    public static final String BLOCKER = "blocker";
    public static final String CRITICAL = "critical";
    public static final String MINOR = "minor";

    private static final Map<String, SeverityLevel> SEVERITY = Map.of(
            BLOCKER, SeverityLevel.BLOCKER,
            CRITICAL, SeverityLevel.CRITICAL,
            MINOR, SeverityLevel.MINOR);

    private Groups() {
    }

    public static Optional<SeverityLevel> severityOf(String[] groups) {
        for (String group : groups) {
            SeverityLevel level = SEVERITY.get(group);
            if (level != null) {
                return Optional.of(level);
            }
        }
        return Optional.empty();
    }
}
