package io.github.alexxfromgit.taf.mobile.examples.mydemo.data;

/**
 * Payment data for the demo app. Uses the well-known, publicly documented test card number that payment
 * providers publish for testing - never put real card data into tests.
 */
public record PaymentCard(String holder, String number, String expiry, String securityCode) {

    public static PaymentCard testVisa() {
        return new PaymentCard("Ada Tester", "4111111111111111", "1230", "123");
    }
}
