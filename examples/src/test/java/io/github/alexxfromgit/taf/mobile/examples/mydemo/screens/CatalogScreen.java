package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.appium.java_client.AppiumBy;
import io.github.alexxfromgit.taf.mobile.core.failure.TestDataException;
import io.github.alexxfromgit.taf.mobile.core.gesture.Gestures;
import io.github.alexxfromgit.taf.mobile.core.screen.ComponentList;
import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;
import org.openqa.selenium.By;

import java.util.List;

/** Start screen: the product catalog. */
public class CatalogScreen extends Screen implements HasHeader {

    @ScreenIdentifier
    @Locate(value = "Catalog-screen", android = @Using("Displays all products of catalog"))
    private UiElement catalog;

    /** The "Products" heading scrolls with the grid: visible means the catalog is at the top. */
    @Locate(value = "Catalog-screen", android = @Using(how = How.ID, value = "productTV"))
    private UiElement heading;

    @Locate(value = "ProductItem", android = @Using(how = How.XPATH,
            value = "//*[@resource-id='com.saucelabs.mydemoapp.android:id/productRV']/android.view.ViewGroup"))
    private ComponentList<ProductTile> products;

    public ComponentList<ProductTile> products() {
        return products.waitAtLeast(1);
    }

    /** The app restores its last scroll position; tests start from the top of the catalog. */
    public CatalogScreen scrollToTop() {
        gestures().scrollUntil(heading::isDisplayed, 15, Gestures.Direction.UP);
        return this;
    }

    /** Tiles whose title and price are fully on screen. */
    public List<ProductTile> visibleProducts() {
        return products().stream().filter(ProductTile::isFullyVisible).toList();
    }

    /** Titles of the products fully on screen. */
    public List<String> visibleTitles() {
        return visibleProducts().stream().map(ProductTile::title).toList();
    }

    /**
     * Opens a product by its exact title, scrolling the catalog if needed. The image is located through the
     * title text (no list index), because the grid recycles its views while scrolling.
     */
    public ProductScreen openProduct(String title) {
        scrollToTop();
        UiElement image = element("image of '" + title + "'",
                By.xpath("//android.view.ViewGroup[./*[@content-desc='Product Title' and @text='" + title + "']]"
                        + "/*[@content-desc='Product Image']"),
                AppiumBy.iOSNsPredicateString("label == '" + title + "'"));
        if (!gestures().scrollUntil(image::isDisplayed, 10)) {
            throw new TestDataException("Product '" + title + "' is not in the catalog");
        }
        ProductScreen product = image.tapAndExpect(ProductScreen.class);
        if (!product.title().equals(title)) {
            throw new TestDataException("Opened '" + product.title() + "' instead of '" + title + "'");
        }
        return product;
    }
}
