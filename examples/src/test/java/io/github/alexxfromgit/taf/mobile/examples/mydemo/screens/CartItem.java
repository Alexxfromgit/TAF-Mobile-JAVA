package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.Component;
import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

/** One line of the cart. */
public class CartItem extends Component {

    @Locate(value = "Product Name", android = @Using(how = How.ID, value = "titleTV"))
    private UiElement title;

    @Locate(value = "Product Price", android = @Using(how = How.ID, value = "priceTV"))
    private UiElement price;

    @Locate(value = "Remove Item", android = @Using("Removes product from cart"))
    private UiElement remove;

    public String title() {
        return title.text();
    }

    public String price() {
        return price.text();
    }

    public void remove() {
        remove.tap();
    }
}
