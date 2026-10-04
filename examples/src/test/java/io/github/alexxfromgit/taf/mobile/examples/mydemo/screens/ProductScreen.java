package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

/** Product details with quantity and "Add to cart". */
public class ProductScreen extends Screen implements HasHeader {

    @ScreenIdentifier
    @Locate(value = "ProductDetails-screen", android = @Using(how = How.ID, value = "productTV"))
    private UiElement title;

    @Locate(value = "Price", android = @Using(how = How.ID, value = "priceTV"))
    private UiElement price;

    @Locate(value = "AddToCart", android = @Using("Tap to add product to cart"))
    private UiElement addToCart;

    @Locate(value = "AddPlus Icons", android = @Using("Increase item quantity"))
    private UiElement increaseQuantity;

    public String title() {
        return title.text();
    }

    public String price() {
        return price.text();
    }

    public ProductScreen increaseQuantity(int times) {
        gestures().scrollUntil(increaseQuantity::isDisplayed, 3);
        for (int i = 0; i < times; i++) {
            increaseQuantity.tap();
        }
        return this;
    }

    public ProductScreen addToCart() {
        gestures().scrollUntil(addToCart::isDisplayed, 3);
        addToCart.tap();
        return this;
    }
}
