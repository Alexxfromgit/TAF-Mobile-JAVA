package io.github.alexxfromgit.taf.mobile.examples.mydemo.tests;

import io.github.alexxfromgit.taf.mobile.core.MobileTestBase;
import io.github.alexxfromgit.taf.mobile.core.session.ResetAppData;
import io.github.alexxfromgit.taf.mobile.core.testng.Groups;
import io.github.alexxfromgit.taf.mobile.core.verify.Verify;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.preconditions.CartContains;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CartScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CatalogScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.ProductScreen;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import static io.github.alexxfromgit.taf.mobile.examples.mydemo.Products.BACKPACK;
import static io.github.alexxfromgit.taf.mobile.examples.mydemo.support.Owners.SHOP_TEAM;

@Epic("My Demo App")
@Feature("Cart")
@Owner(SHOP_TEAM)
@ResetAppData   // every cart test starts with an empty cart
public class CartTest extends MobileTestBase {

    @Test(groups = Groups.SMOKE, description = "Adding a product updates the cart badge")
    public void addingProductUpdatesBadge() {
        ProductScreen product = on(CatalogScreen.class).openProduct(BACKPACK);

        product.increaseQuantity(1).addToCart();

        Verify.equal(product.header().cartCount(), 2, "Items in cart");
    }

    @Test(description = "Cart lists the product added before the test, with its price")
    @CartContains(BACKPACK)
    public void cartListsAddedProduct() {
        CartScreen cart = on(CatalogScreen.class).header().openCart();

        Verify.softly().assertThat(cart.itemTitles()).containsExactly(BACKPACK);
        Verify.softly().assertThat(cart.total()).isEqualTo("$ 29.99");
    }

    @Test(description = "Removing the last product shows the empty cart")
    @CartContains(BACKPACK)
    public void removingLastProductEmptiesCart() {
        CartScreen cart = on(CatalogScreen.class).header().openCart();

        cart.items().get(0).remove();

        Verify.that(cart.isEmpty(), "Cart shows the empty state");
    }
}
