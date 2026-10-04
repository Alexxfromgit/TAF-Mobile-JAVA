package io.github.alexxfromgit.taf.mobile.examples.mydemo.tests;

import io.github.alexxfromgit.taf.mobile.core.MobileTestBase;
import io.github.alexxfromgit.taf.mobile.core.log.Log;
import io.github.alexxfromgit.taf.mobile.core.session.ResetAppData;
import io.github.alexxfromgit.taf.mobile.core.testng.Groups;
import io.github.alexxfromgit.taf.mobile.core.users.TestUser;
import io.github.alexxfromgit.taf.mobile.core.verify.Verify;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.data.Address;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.data.PaymentCard;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.preconditions.CartContains;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.preconditions.LoggedIn;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CatalogScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.CheckoutCompleteScreen;
import io.github.alexxfromgit.taf.mobile.examples.mydemo.screens.ReviewOrderScreen;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import static io.github.alexxfromgit.taf.mobile.examples.mydemo.Products.BACKPACK;
import static io.github.alexxfromgit.taf.mobile.examples.mydemo.support.Owners.SHOP_TEAM;

/**
 * End-to-end purchase. Preconditions bring the app into "logged in with a backpack in the cart", so the test
 * body only contains the behaviour under test.
 */
@Epic("My Demo App")
@Feature("Checkout")
@Owner(SHOP_TEAM)
public class CheckoutFlowTest extends MobileTestBase {

    @Test(groups = {Groups.SMOKE, Groups.BLOCKER}, description = "Logged-in user buys a product")
    @TestUser("standard")
    @ResetAppData
    @LoggedIn
    @CartContains(BACKPACK)
    public void loggedInUserCompletesCheckout() {
        ReviewOrderScreen review = on(CatalogScreen.class)
                .header().openCart()
                .proceedToCheckout()
                .enter(Address.sample())
                .enter(PaymentCard.testVisa());
        Log.info("Order total: {}", review.total());

        CheckoutCompleteScreen complete = review.placeOrder();

        Verify.that(complete.isOpen(), "Checkout complete screen is shown");
        Verify.equal(complete.continueShopping().header().cartCount(), 0, "Cart is empty after the order");
    }
}
