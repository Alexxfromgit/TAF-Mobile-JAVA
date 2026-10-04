package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.data.PaymentCard;

/** Checkout step 2: payment method (demo app - only published test card numbers). */
public class CheckoutPaymentScreen extends Screen {

    @ScreenIdentifier
    @Locate(value = "Payment-screen", android = @Using(how = How.ID, value = "cardNumberET"))
    private UiElement screen;

    @Locate(android = @Using(how = How.ID, value = "nameET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[1]"))
    private UiElement cardHolder;

    @Locate(android = @Using(how = How.ID, value = "cardNumberET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[2]"))
    private UiElement cardNumber;

    @Locate(android = @Using(how = How.ID, value = "expirationDateET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[3]"))
    private UiElement expiration;

    @Locate(android = @Using(how = How.ID, value = "securityCodeET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[4]"))
    private UiElement securityCode;

    @Locate(android = @Using("Saves payment info and launches screen to review checkout data"),
            ios = @Using(how = How.TEXT, value = "Review Order"))
    private UiElement reviewOrder;

    public ReviewOrderScreen enter(PaymentCard card) {
        cardHolder.type(card.holder());
        cardNumber.type(card.number());
        expiration.type(card.expiry());
        securityCode.type(card.securityCode());
        gestures().scrollUntil(reviewOrder::isDisplayed, 3);
        return reviewOrder.tapAndExpect(ReviewOrderScreen.class);
    }
}
