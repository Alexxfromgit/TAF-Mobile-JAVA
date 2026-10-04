package io.github.alexxfromgit.taf.mobile.core.screen;

import io.appium.java_client.AppiumBy;
import io.github.alexxfromgit.taf.mobile.core.driver.Platform;
import io.github.alexxfromgit.taf.mobile.core.failure.ElementNotFoundException;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.mobile.core.failure.ScreenNotReadyException;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScreenFactoryTest {

    // --- fixtures: a screen with a header component, a product list and a platform-specific locator ---------

    @Locate(android = @Using(how = How.ID, value = "header"), ios = @Using("Header"))
    public static class Header extends Component {
        @Locate("View cart")
        private UiElement cart;

        UiElement cart() {
            return cart;
        }
    }

    public static class ProductTile extends Component {
        @Locate("Product Title")
        private UiElement title;

        String title() {
            return title.text();
        }
    }

    public interface HasHeader extends ScreenMixin {
        default Header header() {
            return component(Header.class);
        }
    }

    public static class CatalogScreen extends Screen implements HasHeader {
        @ScreenIdentifier
        @Locate(value = "title", android = @Using(how = How.ID, value = "productTV"))
        private UiElement title;

        @Locate(android = @Using(how = How.ID, value = "productRV"), ios = @Using("ProductItem"))
        private ComponentList<ProductTile> products;

        @Locate("Sort")
        private UiElements sortOptions;

        UiElement title() {
            return title;
        }

        ComponentList<ProductTile> products() {
            return products;
        }
    }

    public static class BrokenScreen extends Screen {
        @Locate(android = @Using(how = How.ID, value = "onlyAndroid"))
        private UiElement androidOnly;
    }

    // ----------------------------------------------------------------------------------------------------------

    private SearchContext driver;

    @BeforeMethod
    public void mockDriver() {
        driver = mock(SearchContext.class);
        when(driver.findElements(any(By.class))).thenReturn(List.of());
    }

    @Test
    public void platformSpecificLocatorWinsOverAccessibilityId() {
        CatalogScreen android = ScreenFactory.create(CatalogScreen.class, new UiContext(() -> driver, Platform.ANDROID));
        CatalogScreen ios = ScreenFactory.create(CatalogScreen.class, new UiContext(() -> driver, Platform.IOS));

        assertThat(android.title().by()).isEqualTo(AppiumBy.id("productTV"));
        assertThat(ios.title().by()).isEqualTo(AppiumBy.accessibilityId("title"));
        assertThat(android.title().name()).isEqualTo("CatalogScreen.title");
    }

    @Test
    public void waitReadySucceedsWhenIdentifiersAreVisible() {
        WebElement title = visible();
        when(driver.findElements(AppiumBy.id("productTV"))).thenReturn(List.of(title));

        CatalogScreen screen = ScreenFactory.create(CatalogScreen.class, new UiContext(() -> driver, Platform.ANDROID));

        assertThat(screen.<CatalogScreen>waitReady()).isSameAs(screen);
        assertThat(screen.isOpen()).isTrue();
    }

    @Test
    public void waitReadyNamesTheMissingIdentifier() {
        CatalogScreen screen = ScreenFactory.create(CatalogScreen.class, new UiContext(() -> driver, Platform.ANDROID));

        assertThatThrownBy(screen::waitReady)
                .isInstanceOf(ScreenNotReadyException.class)
                .hasMessageContaining("CatalogScreen is not ready")
                .hasMessageContaining("CatalogScreen.title is not visible")
                .hasMessageContaining("productTV");
    }

    @Test
    public void componentsSearchInsideTheirRoot() {
        WebElement header = visible();
        WebElement cart = visible();
        when(driver.findElements(AppiumBy.id("header"))).thenReturn(List.of(header));
        when(header.findElements(AppiumBy.accessibilityId("View cart"))).thenReturn(List.of(cart));

        CatalogScreen screen = ScreenFactory.create(CatalogScreen.class, new UiContext(() -> driver, Platform.ANDROID));
        screen.header().cart().tap();

        verify(cart).click();
        assertThat(screen.header().cart().name()).isEqualTo("CatalogScreen.header.cart");
    }

    @Test
    public void listItemsAreScopedToTheirOwnRoot() {
        WebElement first = visible();
        WebElement firstTitle = visible();
        WebElement second = visible();
        WebElement secondTitle = visible();
        when(firstTitle.getText()).thenReturn("Backpack");
        when(secondTitle.getText()).thenReturn("Bike Light");
        when(first.findElements(AppiumBy.accessibilityId("Product Title"))).thenReturn(List.of(firstTitle));
        when(driver.findElements(AppiumBy.id("productRV"))).thenReturn(List.of(first, second));
        when(second.findElements(AppiumBy.accessibilityId("Product Title"))).thenReturn(List.of(secondTitle));

        CatalogScreen screen = ScreenFactory.create(CatalogScreen.class, new UiContext(() -> driver, Platform.ANDROID));

        assertThat(screen.products().size()).isEqualTo(2);
        assertThat(screen.products().get(1).title()).isEqualTo("Bike Light");
        assertThat(screen.products().first(p -> p.title().equals("Bike Light")).name())
                .isEqualTo("CatalogScreen.products[1]");
    }

    @Test
    public void missingElementFailsWithFieldNameAndLocator() {
        CatalogScreen screen = ScreenFactory.create(CatalogScreen.class, new UiContext(() -> driver, Platform.ANDROID));

        assertThatThrownBy(() -> screen.title().tap())
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage("CatalogScreen.title is not visible after 300ms [AppiumBy.id: productTV]");
    }

    @Test
    public void missingLocatorForPlatformFailsFast() {
        assertThatThrownBy(() -> ScreenFactory.create(BrokenScreen.class, new UiContext(() -> driver, Platform.IOS)))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("BrokenScreen.androidOnly has no locator for IOS");
    }

    @Test
    public void textLocatorIsCrossPlatform() {
        assertThat(LocatorResolver.toBy(How.TEXT, "Log In", Platform.ANDROID, "x").toString())
                .contains("new UiSelector().text(\"Log In\")");
        assertThat(LocatorResolver.toBy(How.TEXT, "Log In", Platform.IOS, "x").toString())
                .contains("label == \"Log In\"");
        assertThatThrownBy(() -> LocatorResolver.toBy(How.IOS_PREDICATE, "x", Platform.ANDROID, "S.f"))
                .isInstanceOf(FrameworkException.class)
                .hasMessageContaining("only works on IOS");
    }

    private static WebElement visible() {
        WebElement element = mock(WebElement.class);
        when(element.isDisplayed()).thenReturn(true);
        when(element.findElements(any(By.class))).thenReturn(List.of());
        return element;
    }
}
