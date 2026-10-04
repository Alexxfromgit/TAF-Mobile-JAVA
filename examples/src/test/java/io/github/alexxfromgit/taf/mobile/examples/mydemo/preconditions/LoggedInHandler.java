package io.github.alexxfromgit.taf.mobile.examples.mydemo.preconditions;

import io.github.alexxfromgit.taf.mobile.core.failure.TestDataException;
import io.github.alexxfromgit.taf.mobile.core.precondition.PreconditionHandler;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenFactory;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CatalogScreen;

/**
 * Logs in through the UI. With a real app, prefer a faster route if it has one: a deep link with a session
 * token, a launch argument, or creating the session through the backend API.
 */
public class LoggedInHandler implements PreconditionHandler<LoggedIn> {

    @Override
    public void apply(LoggedIn annotation, Context context) {
        var user = context.user().orElseThrow(() -> new TestDataException(
                "@LoggedIn needs @TestUser on " + context.testMethod().getName()));
        ScreenFactory.create(CatalogScreen.class).<CatalogScreen>waitReady()
                .header().openMenu()
                .openLogin()
                .loginAs(user, CatalogScreen.class);
    }
}
