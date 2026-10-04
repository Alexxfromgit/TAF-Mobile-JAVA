package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

/** Navigation drawer (Android) / "More" tab (iOS). */
public class MenuScreen extends Screen {

    @ScreenIdentifier
    @Locate(android = @Using("Recycler view for menu"),
            ios = @Using(how = How.CLASS_NAME, value = "XCUIElementTypeTable"))
    private UiElement menu;

    @Locate(android = @Using("Login Menu Item"), ios = @Using(how = How.TEXT, value = "Log In"))
    private UiElement login;

    @Locate(android = @Using("Logout Menu Item"), ios = @Using(how = How.TEXT, value = "Log Out"))
    private UiElement logout;

    @Locate(android = @Using(how = How.TEXT, value = "Catalog"), ios = @Using(how = How.TEXT, value = "Catalog"))
    private UiElement catalog;

    public LoginScreen openLogin() {
        return login.tapAndExpect(LoginScreen.class);
    }

    public CatalogScreen openCatalog() {
        return catalog.tapAndExpect(CatalogScreen.class);
    }

    public boolean isLoggedIn() {
        return logout.isDisplayed();
    }
}
