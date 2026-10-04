package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

/** "Checkout Complete". */
public class CheckoutCompleteScreen extends Screen {

    @ScreenIdentifier
    @Locate(value = "CheckoutComplete-screen", android = @Using(how = How.ID, value = "completeTV"))
    private UiElement title;

    @Locate(value = "ContinueShopping", android = @Using("Tap to open catalog"))
    private UiElement continueShopping;

    public CatalogScreen continueShopping() {
        return continueShopping.tapAndExpect(CatalogScreen.class);
    }
}
