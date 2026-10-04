package io.github.alexxfromgit.taf.mobile.examples.mydemo;

/**
 * Products of the Sauce Labs demo catalog used by the examples.
 * <p>
 * Known bugs of My Demo App 2.3.0 (Android), found by these tests:
 * <ul>
 *     <li>the catalog's click handler works with a stale 6-item list: opening the 7th or later product crashes the
 *     app ({@code ArrayIndexOutOfBoundsException: length=6});</li>
 *     <li>after returning to the catalog (menu or back) in the same launch, tapping a product opens the wrong one
 *     or crashes the app ({@code NullPointerException}).</li>
 * </ul>
 * The examples therefore open one product per app launch, from the first six.
 */
public final class Products {

    public static final String BACKPACK = "Sauce Labs Backpack";

    private Products() {
    }
}
