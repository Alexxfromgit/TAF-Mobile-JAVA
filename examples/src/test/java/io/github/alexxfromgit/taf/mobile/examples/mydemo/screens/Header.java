package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.Component;
import io.github.alexxfromgit.taf.mobile.core.screen.How;
import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.github.alexxfromgit.taf.mobile.core.screen.Using;

/**
 * App navigation: the top bar on Android, the tab bar on iOS. One component, two layouts - test code
 * does not care which one is on screen.
 */
// the layout root is "headerCL", but the <include> in the activity renames it to "header" at runtime
@Locate(android = @Using(how = How.ID, value = "header"),
        ios = @Using(how = How.CLASS_NAME, value = "XCUIElementTypeTabBar"))
public class Header extends Component {

    @Locate(value = "Cart-tab-item", android = @Using("View cart"))
    private UiElement cart;

    @Locate(value = "More-tab-item", android = @Using("View menu"))
    private UiElement menu;

    @Locate(android = @Using(how = How.ID, value = "cartTV"),
            ios = @Using(how = How.IOS_PREDICATE, value = "type == 'XCUIElementTypeStaticText' AND name MATCHES '[0-9]+'"))
    private UiElement cartBadge;

    public CartScreen openCart() {
        return cart.tapAndExpect(CartScreen.class);
    }

    public MenuScreen openMenu() {
        return menu.tapAndExpect(MenuScreen.class);
    }

    /** Number shown on the cart icon; 0 when the badge is hidden. */
    public int cartCount() {
        return cartBadge.isDisplayed() ? Integer.parseInt(cartBadge.text().trim()) : 0;
    }
}
