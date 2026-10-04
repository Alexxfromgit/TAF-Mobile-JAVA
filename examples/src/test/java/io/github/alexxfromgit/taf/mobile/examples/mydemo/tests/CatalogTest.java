package io.github.alexxfromgit.taf.mobile.examples.mydemo.tests;

import io.github.alexxfromgit.taf.mobile.core.MobileTestBase;
import io.github.alexxfromgit.taf.mobile.core.testng.Groups;
import io.github.alexxfromgit.taf.mobile.core.verify.Verify;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CatalogScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.ProductScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.ProductTile;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import static io.github.alexxfromgit.taf.mobile.examples.mydemo.support.Owners.SHOP_TEAM;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("My Demo App")
@Feature("Catalog")
@Owner(SHOP_TEAM)
public class CatalogTest extends MobileTestBase {

    @Test(groups = Groups.SMOKE, description = "Catalog lists products with title and price")
    public void catalogListsProducts() {
        CatalogScreen catalog = on(CatalogScreen.class).scrollToTop();

        assertThat(catalog.visibleTitles()).isNotEmpty().allMatch(title -> !title.isBlank());
        catalog.visibleProducts().forEach(tile ->
                Verify.that(tile.price().matches("\\$ ?\\d+\\.\\d{2}"), tile.title() + " has a price like $ 29.99"));
    }

    @Test(groups = Groups.SMOKE, description = "Product details match the tile that was tapped")
    public void productDetailsMatchTheTile() {
        ProductTile tile = on(CatalogScreen.class).scrollToTop().products().get(0);
        String title = tile.title();
        String price = tile.price();

        ProductScreen product = tile.open();

        Verify.equal(product.title(), title, "Product title");
        Verify.equal(product.price(), price, "Product price");
    }
}
