package io.github.alexxfromgit.taf.mobile.core.screen;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.ElementNotFoundException;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.mobile.core.failure.ScreenNotReadyException;
import io.github.alexxfromgit.taf.mobile.core.perf.PerfCollector;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A page object for one app screen. Fields are injected by {@link ScreenFactory}:
 * <pre>{@code
 * @WaitFor(timeout = "20s")
 * public class CatalogScreen extends Screen implements HasHeader {
 *
 *     @ScreenIdentifier
 *     @Locate(android = @Using(how = How.ID, value = "productTV"), ios = @Using("Catalog-screen"))
 *     private UiElement title;
 *
 *     @Locate(android = @Using(how = How.ID, value = "productRV"), ...)
 *     private ComponentList<ProductTile> products;
 *
 *     public ProductScreen openProduct(int index) {
 *         return products.get(index).image().tapAndExpect(ProductScreen.class);
 *     }
 * }
 * }</pre>
 * Navigation methods return the next screen, verification methods return {@code this}: tests read as flows.
 */
public abstract class Screen extends UiContainer {

    /**
     * Waits until the screen is ready: {@link WaitFor#gone()} elements disappeared, then all
     * {@link ScreenIdentifier} fields are visible. Records the time in the performance report.
     */
    @SuppressWarnings("unchecked")
    public <S extends Screen> S waitReady() {
        WaitFor waitFor = getClass().getAnnotation(WaitFor.class);
        Duration timeout = waitFor != null && !waitFor.timeout().isBlank()
                ? TafConfig.parseDuration(waitFor.timeout())
                : TafConfig.get().duration("wait.screen-timeout");
        long start = System.nanoTime();
        try {
            if (waitFor != null) {
                for (Locate gone : waitFor.gone()) {
                    new UiElement(name(), "loading indicator",
                            LocatorResolver.resolve(gone, platform(), name() + " @WaitFor(gone)"),
                            context().searchContext()).waitGone(timeout);
                }
            }
            if (waitFor == null || waitFor.identifiers()) {
                for (UiElement identifier : identifiers()) {
                    identifier.waitVisible(timeout);
                }
            }
        } catch (ElementNotFoundException e) {
            throw new ScreenNotReadyException(name() + " is not ready after " + Waits.format(timeout) + ": "
                    + e.getMessage(), e);
        }
        PerfCollector.screenReady(name(), (System.nanoTime() - start) / 1_000_000);
        return (S) this;
    }

    /** Whether all identifiers are visible right now (no waiting). */
    public boolean isOpen() {
        List<UiElement> identifiers = identifiers();
        return !identifiers.isEmpty() && identifiers.stream().allMatch(UiElement::isDisplayed);
    }

    List<UiElement> identifiers() {
        List<UiElement> result = new ArrayList<>();
        for (Class<?> type = getClass(); type != Screen.class; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(ScreenIdentifier.class)) {
                    if (field.getType() != UiElement.class) {
                        throw new FrameworkException("@ScreenIdentifier " + name() + "." + field.getName()
                                + " must be a UiElement");
                    }
                    try {
                        field.setAccessible(true);
                        result.add((UiElement) field.get(this));
                    } catch (IllegalAccessException e) {
                        throw new FrameworkException("Cannot read " + field, e);
                    }
                }
            }
        }
        return result;
    }
}
