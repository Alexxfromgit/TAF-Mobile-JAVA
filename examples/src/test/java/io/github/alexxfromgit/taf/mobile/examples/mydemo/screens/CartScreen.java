package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.ComponentList;
import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

import java.util.List;

/** "My Cart": the items or the empty state. */
public class CartScreen extends Screen implements HasHeader {

    @ScreenIdentifier
    // "My Cart" when there are items, "No Items" when empty
    @Locate(value = "Cart-screen", android = @Using(how = How.XPATH, value = "//*[@text='My Cart' or @text='No Items']"))
    private UiElement title;

    @Locate(value = "ProductItem", android = @Using(how = How.XPATH,
            value = "//*[@resource-id='com.saucelabs.mydemoapp.android:id/productRV']/android.view.ViewGroup"))
    private ComponentList<CartItem> items;

    @Locate(value = "ProceedToCheckout", android = @Using("Confirms products for checkout"))
    private UiElement proceedToCheckout;

    @Locate(value = "GoShopping", android = @Using(how = How.ID, value = "shoppingBt"))
    private UiElement goShopping;

    @Locate(value = "Amount", android = @Using(how = How.ID, value = "totalPriceTV"))
    private UiElement total;

    public ComponentList<CartItem> items() {
        return items;
    }

    public List<String> itemTitles() {
        return items.stream().map(CartItem::title).toList();
    }

    /** Waits briefly for the empty state ("Go Shopping"). */
    public boolean isEmpty() {
        try {
            goShopping.waitVisible();
            return true;
        } catch (AssertionError notEmpty) {
            return false;
        }
    }

    public String total() {
        return total.text();
    }

    /** Logged in: goes straight to the shipping address. */
    public CheckoutAddressScreen proceedToCheckout() {
        return proceedToCheckout.tapAndExpect(CheckoutAddressScreen.class);
    }

    /** Not logged in: the app asks to log in first. */
    public LoginScreen proceedToCheckoutAsGuest() {
        return proceedToCheckout.tapAndExpect(LoginScreen.class);
    }
}
