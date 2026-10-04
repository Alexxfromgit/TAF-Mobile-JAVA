package io.github.alexxfromgit.taf.mobile.core.session;

/**
 * How a test starts. Cheaper modes keep suites fast; {@link SessionPolicy} picks the cheapest safe one.
 */
public enum StartMode {
    /** Quit the Appium session and start a new one (slowest, cleanest). */
    NEW_SESSION,
    /** Wipe app data (logged-in user, cart, settings) and relaunch the app in the existing session. */
    RESET_APP_DATA,
    /** Terminate and relaunch the app: back to the start screen, data kept. */
    RESTART_APP,
    /** Continue where the previous test stopped (only for tests designed as a chain). */
    REUSE
}
