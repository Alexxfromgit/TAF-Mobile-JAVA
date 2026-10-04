package io.github.alexxfromgit.taf.mobile.examples.mydemo.preconditions;

import io.github.alexxfromgit.taf.mobile.core.precondition.PreconditionHandler;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenFactory;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CatalogScreen;

/**
 * Adds products to the cart through the UI and returns to the catalog. The cart lives in memory only, so the
 * app must not be restarted in between. With the demo app, use one product per test: see {@code Products} for the
 * catalog bugs that make a second product in the same launch unreliable.
 */
public class CartContainsHandler implements PreconditionHandler<CartContains> {

    @Override
    public void apply(CartContains annotation, Context context) {
        for (String product : annotation.value()) {
            ScreenFactory.create(CatalogScreen.class).<CatalogScreen>waitReady()
                    .openProduct(product)
                    .addToCart()
                    .header().openMenu()
                    .openCatalog();
        }
    }
}
