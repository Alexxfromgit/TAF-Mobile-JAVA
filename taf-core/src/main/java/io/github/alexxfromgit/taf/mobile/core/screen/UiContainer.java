package io.github.alexxfromgit.taf.mobile.core.screen;

import io.appium.java_client.AppiumDriver;
import io.github.alexxfromgit.taf.mobile.core.driver.DriverManager;
import io.github.alexxfromgit.taf.mobile.core.driver.Platform;
import io.github.alexxfromgit.taf.mobile.core.gesture.Gestures;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

/** Common base of {@link Screen} and {@link Component}: element injection context, name and helpers. */
public abstract class UiContainer implements ScreenMixin {

    private String name;
    private UiContext context;

    final void init(String name, UiContext context) {
        this.name = name;
        this.context = context;
    }

    /** Readable name used in reports and errors, e.g. {@code CatalogScreen} or {@code CatalogScreen.products[2]}. */
    public String name() {
        return name;
    }

    public Platform platform() {
        return context.platform();
    }

    UiContext context() {
        return context;
    }

    protected SearchContext searchContext() {
        return context.searchContext().get();
    }

    protected AppiumDriver driver() {
        return DriverManager.driver();
    }

    protected Gestures gestures() {
        return new Gestures(driver());
    }

    /** A component declared by a class-level {@link Locate}, searched inside this container. */
    @Override
    public <C extends Component> C component(Class<C> type) {
        return ScreenFactory.component(type, this);
    }

    /**
     * An element whose locator is only known at runtime, e.g. "the row with this text". Prefer this over picking
     * list items by index after scrolling: recycled list views can reorder their children.
     */
    protected UiElement element(String name, By android, By ios) {
        return new UiElement(name(), name, platform() == Platform.ANDROID ? android : ios, context.searchContext());
    }

    /** Creates {@code type} and waits until it is ready - for navigation that is not a single tap. */
    protected <S extends Screen> S expect(Class<S> type) {
        return ScreenFactory.create(type).waitReady();
    }
}
