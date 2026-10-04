package io.github.alexxfromgit.taf.mobile.examples.mydemo.data;

/** Shipping address used by checkout tests. Fictional data only. */
public record Address(String fullName, String line1, String city, String zip, String country) {

    public static Address sample() {
        return new Address("Ada Tester", "1 Test Street", "Testville", "12345", "Testland");
    }
}
