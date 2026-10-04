package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;
import io.github.alexxfromgit.taf.mobile.core.users.UserCredentials;

/** Login form. */
public class LoginScreen extends Screen {

    @ScreenIdentifier
    @Locate(android = @Using("Tap to login with given credentials"), ios = @Using(how = How.TEXT, value = "Login"))
    private UiElement loginButton;

    @Locate(android = @Using(how = How.ID, value = "nameET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField"))
    private UiElement username;

    @Locate(android = @Using(how = How.ID, value = "passwordET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeSecureTextField"))
    private UiElement password;

    @Locate(android = @Using(how = How.ID, value = "passwordErrorTV"),
            ios = @Using(how = How.IOS_PREDICATE, value = "label CONTAINS 'locked out' OR label CONTAINS 'Password'"))
    private UiElement passwordError;

    @Locate(android = @Using(how = How.ID, value = "nameErrorTV"),
            ios = @Using(how = How.IOS_PREDICATE, value = "label CONTAINS 'Username is required'"))
    private UiElement usernameError;

    public LoginScreen fill(String user, String secret) {
        username.type(user);
        password.type(secret);
        return this;
    }

    /** Logs in and lands wherever the app goes next (catalog from the menu, checkout from the cart). */
    public <S extends Screen> S loginAs(UserCredentials user, Class<S> next) {
        fill(user.username(), user.password());
        return loginButton.tapAndExpect(next);
    }

    /** Submits and stays on the login screen (validation errors). */
    public LoginScreen submitExpectingError() {
        loginButton.tap();
        return this;
    }

    public String passwordError() {
        return passwordError.text();
    }

    public String usernameError() {
        return usernameError.text();
    }
}
