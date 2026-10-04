package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.Component;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

/** One product of the catalog grid. Its elements are searched inside the tile only. */
public class ProductTile extends Component {

    @Locate(value = "Product Name", android = @Using("Product Title"))
    private UiElement title;

    @Locate("Product Price")
    private UiElement price;

    @Locate("Product Image")
    private UiElement image;

    public String title() {
        return title.text();
    }

    public String price() {
        return price.text();
    }

    /** True when title and price are on screen (the last tile of the grid is often cut off). */
    public boolean isFullyVisible() {
        return title.isDisplayed() && price.isDisplayed();
    }

    public ProductScreen open() {
        return image.tapAndExpect(ProductScreen.class);
    }
}
