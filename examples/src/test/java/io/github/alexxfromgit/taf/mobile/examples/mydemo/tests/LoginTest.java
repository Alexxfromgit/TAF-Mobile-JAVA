package io.github.alexxfromgit.taf.mobile.examples.mydemo.tests;

import io.github.alexxfromgit.taf.mobile.core.MobileTestBase;
import io.github.alexxfromgit.taf.mobile.core.testng.Groups;
import io.github.alexxfromgit.taf.mobile.core.users.TestUser;
import io.github.alexxfromgit.taf.mobile.core.verify.Verify;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CatalogScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.LoginScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.MenuScreen;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import static io.github.alexxfromgit.taf.mobile.examples.mydemo.support.Owners.ACCOUNT_TEAM;

@Epic("My Demo App")
@Feature("Login")
@Owner(ACCOUNT_TEAM)
public class LoginTest extends MobileTestBase {

    @Test(groups = {Groups.SMOKE, Groups.CRITICAL}, description = "Standard user can log in")
    @TestUser("standard")
    public void standardUserCanLogIn() {
        MenuScreen menu = on(CatalogScreen.class)
                .header().openMenu()
                .openLogin()
                .loginAs(user().orElseThrow(), CatalogScreen.class)
                .header().openMenu();

        Verify.that(menu.isLoggedIn(), "Menu offers 'Log Out' after login");
    }

    @Test(description = "Locked-out user sees an explanation")
    @TestUser("locked")
    public void lockedUserIsRejected() {
        LoginScreen login = on(CatalogScreen.class)
                .header().openMenu()
                .openLogin()
                .fill(user().orElseThrow().username(), user().orElseThrow().password())
                .submitExpectingError();

        Verify.equal(login.passwordError(), "Sorry this user has been locked out.", "Error message");
    }

    @Test(description = "Username is required")
    public void usernameIsRequired() {
        LoginScreen login = on(CatalogScreen.class)
                .header().openMenu()
                .openLogin()
                .submitExpectingError();

        Verify.equal(login.usernameError(), "Username is required", "Error message");
    }
}
