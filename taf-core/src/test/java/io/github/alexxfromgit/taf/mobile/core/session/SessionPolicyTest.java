package io.github.alexxfromgit.taf.mobile.core.session;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.github.alexxfromgit.taf.mobile.core.session.StartMode.NEW_SESSION;
import static io.github.alexxfromgit.taf.mobile.core.session.StartMode.RESET_APP_DATA;
import static io.github.alexxfromgit.taf.mobile.core.session.StartMode.RESTART_APP;
import static io.github.alexxfromgit.taf.mobile.core.session.StartMode.REUSE;
import static org.assertj.core.api.Assertions.assertThat;

public class SessionPolicyTest {

    @DataProvider
    public Object[][] decisions() {
        return new Object[][]{
                // hasSession, prevFailed, @FreshSession, @ResetAppData, prevUser, nextUser, default, expected
                {false, false, false, false, null, null, RESTART_APP, NEW_SESSION},
                {true, true, false, false, null, null, RESTART_APP, NEW_SESSION},
                {true, false, true, false, null, null, RESTART_APP, NEW_SESSION},
                {true, false, false, true, null, null, RESTART_APP, RESET_APP_DATA},
                {true, false, false, false, "standard", "admin", RESTART_APP, RESET_APP_DATA},
                {true, false, false, false, null, "standard", RESTART_APP, RESET_APP_DATA},
                {true, false, false, false, "standard", "standard", RESTART_APP, RESTART_APP},
                {true, false, false, false, null, null, REUSE, REUSE},
                {true, false, false, false, "a", "b", REUSE, RESET_APP_DATA},
                {true, false, false, false, "a", "b", NEW_SESSION, NEW_SESSION},
        };
    }

    @Test(dataProvider = "decisions")
    public void decidesCheapestSafeStartMode(boolean hasSession, boolean previousFailed, boolean fresh, boolean reset,
                                             String previousUser, String nextUser, StartMode defaultMode,
                                             StartMode expected) {
        SessionPolicy.Situation situation = new SessionPolicy.Situation(
                hasSession, previousFailed, fresh, reset, previousUser, nextUser);

        assertThat(SessionPolicy.decide(situation, defaultMode, NEW_SESSION)).isEqualTo(expected);
    }

    @Test
    public void afterFailureModeIsConfigurable() {
        SessionPolicy.Situation failed = new SessionPolicy.Situation(true, true, false, false, null, null);
        assertThat(SessionPolicy.decide(failed, RESTART_APP, RESET_APP_DATA)).isEqualTo(RESET_APP_DATA);
    }
}
