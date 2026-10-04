package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.data.Address;

/** Checkout step 1: shipping address. */
public class CheckoutAddressScreen extends Screen {

    @ScreenIdentifier
    @Locate(value = "ShippingAddress-screen", android = @Using(how = How.ID, value = "fullNameET"))
    private UiElement screen;

    @Locate(android = @Using(how = How.ID, value = "fullNameET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[1]"))
    private UiElement fullName;

    @Locate(android = @Using(how = How.ID, value = "address1ET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[2]"))
    private UiElement address1;

    @Locate(android = @Using(how = How.ID, value = "cityET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[3]"))
    private UiElement city;

    @Locate(android = @Using(how = How.ID, value = "zipET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[4]"))
    private UiElement zip;

    @Locate(android = @Using(how = How.ID, value = "countryET"),
            ios = @Using(how = How.IOS_CLASS_CHAIN, value = "**/XCUIElementTypeTextField[5]"))
    private UiElement country;

    @Locate(android = @Using("Saves user info for checkout"), ios = @Using(how = How.TEXT, value = "To Payment"))
    private UiElement toPayment;

    public CheckoutPaymentScreen enter(Address address) {
        fullName.type(address.fullName());
        address1.type(address.line1());
        city.type(address.city());
        gestures().scrollUntil(zip::isDisplayed, 3);
        zip.type(address.zip());
        gestures().scrollUntil(country::isDisplayed, 3);
        country.type(address.country());
        gestures().scrollUntil(toPayment::isDisplayed, 3);
        return toPayment.tapAndExpect(CheckoutPaymentScreen.class);
    }
}
