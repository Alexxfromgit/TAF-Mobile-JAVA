package io.github.alexxfromgit.taf.mobile.core.session;

import java.util.Objects;

/**
 * Decides the {@link StartMode} of the next test. In order of precedence:
 * <ol>
 *     <li>no session yet -> {@code NEW_SESSION};</li>
 *     <li>previous test on this thread failed -> {@code session.after-failure} (default {@code NEW_SESSION}),
 *     because a failure may leave the app or driver in an unknown state;</li>
 *     <li>{@link FreshSession} -> {@code NEW_SESSION};</li>
 *     <li>{@link ResetAppData} or a different {@code @TestUser} than the previous test -> {@code RESET_APP_DATA};</li>
 *     <li>otherwise {@code session.default} (default {@code RESTART_APP}).</li>
 * </ol>
 */
public final class SessionPolicy {

    private SessionPolicy() {
    }

    /** Inputs of the decision. Users are aliases ({@code null} = no {@code @TestUser}). */
    public record Situation(boolean hasSession, boolean previousFailed, boolean freshSession, boolean resetAppData,
                            String previousUser, String nextUser) {
    }

    public static StartMode decide(Situation s, StartMode defaultMode, StartMode afterFailure) {
        if (!s.hasSession()) {
            return StartMode.NEW_SESSION;
        }
        if (s.previousFailed()) {
            return afterFailure;
        }
        if (s.freshSession()) {
            return StartMode.NEW_SESSION;
        }
        if (s.resetAppData() || !Objects.equals(s.previousUser(), s.nextUser())) {
            return stronger(StartMode.RESET_APP_DATA, defaultMode);
        }
        return defaultMode;
    }

    /** The more thorough of two modes (enum order: NEW_SESSION is strongest). */
    private static StartMode stronger(StartMode a, StartMode b) {
        return a.ordinal() <= b.ordinal() ? a : b;
    }
}
