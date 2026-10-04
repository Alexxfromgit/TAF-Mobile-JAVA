package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

/** Checkout step 3: review and place the order. */
public class ReviewOrderScreen extends Screen {

    @ScreenIdentifier
    @Locate(android = @Using(how = How.TEXT, value = "Review your order"),
            ios = @Using(how = How.TEXT, value = "Review your order"))
    private UiElement title;

    @Locate(android = @Using("Completes the process of checkout"), ios = @Using(how = How.TEXT, value = "Place Order"))
    private UiElement placeOrder;

    @Locate(value = "Amount", android = @Using(how = How.ID, value = "totalAmountTV"))
    private UiElement total;

    public String total() {
        gestures().scrollUntil(total::isDisplayed, 3);
        return total.text();
    }

    public CheckoutCompleteScreen placeOrder() {
        gestures().scrollUntil(placeOrder::isDisplayed, 3);
        return placeOrder.tapAndExpect(CheckoutCompleteScreen.class);
    }
}
