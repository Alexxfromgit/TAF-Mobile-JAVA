package io.github.alexxfromgit.taf.mobile.core.screen;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.ElementNotFoundException;
import io.github.alexxfromgit.taf.mobile.core.gesture.Gestures;
import io.github.alexxfromgit.taf.mobile.core.log.Log;
import io.github.alexxfromgit.taf.mobile.core.perf.PerfCollector;
import io.qameta.allure.model.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A named, lazily located element. Every action re-locates the element with an explicit wait, so there are
 * no stale references and no implicit waits. Failures name the screen field and the locator:
 * <pre>CatalogScreen.sortButton is not visible after 10s [By.id: sortIV]</pre>
 */
public class UiElement {

    private final String owner;
    private final String field;
    private final By by;
    private final Supplier<? extends SearchContext> context;
    private final int index;

    public UiElement(String owner, String field, By by, Supplier<? extends SearchContext> context) {
        this(owner, field, by, context, -1);
    }

    UiElement(String owner, String field, By by, Supplier<? extends SearchContext> context, int index) {
        this.owner = owner;
        this.field = field;
        this.by = by;
        this.context = context;
        this.index = index;
    }

    /** {@code Screen.field} or {@code Screen.field[2]}. */
    public String name() {
        return owner + "." + field + (index >= 0 ? "[" + index + "]" : "");
    }

    public By by() {
        return by;
    }

    /** One lookup attempt, without waiting. */
    public Optional<WebElement> findNow() {
        try {
            List<WebElement> all = context.get().findElements(by);
            int i = Math.max(index, 0);
            return all.size() > i ? Optional.of(all.get(i)) : Optional.empty();
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotFoundException e) {
            return Optional.empty();   // e.g. the parent component is not there (yet)
        }
    }

    public boolean isPresent() {
        return findNow().isPresent();
    }

    /** Immediate visibility check, no waiting. */
    public boolean isDisplayed() {
        try {
            return findNow().map(WebElement::isDisplayed).orElse(false);
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    public WebElement waitVisible() {
        return waitVisible(TafConfig.get().duration("wait.timeout"));
    }

    public WebElement waitVisible(Duration timeout) {
        long start = System.nanoTime();
        Optional<WebElement> element = Waits.poll(
                () -> findNow().filter(WebElement::isDisplayed).orElse(null), timeout, poll());
        PerfCollector.lookup(owner, field, (System.nanoTime() - start) / 1_000_000);
        return element.orElseThrow(() -> new ElementNotFoundException(
                name() + " is not visible after " + Waits.format(timeout) + " [" + by + "]", null));
    }

    public UiElement waitGone() {
        return waitGone(TafConfig.get().duration("wait.timeout"));
    }

    public UiElement waitGone(Duration timeout) {
        if (Waits.poll(() -> !isDisplayed(), timeout, poll()).isEmpty()) {
            throw new ElementNotFoundException(name() + " is still visible after " + Waits.format(timeout)
                    + " [" + by + "]", null);
        }
        return this;
    }

    public UiElement tap() {
        WebElement element = waitVisible();
        Log.step("Tap " + name(), Status.PASSED);
        element.click();
        return this;
    }

    /**
     * Taps and waits until {@code next} is ready. The time between tap and ready is recorded as a screen
     * transition in the performance report.
     */
    public <S extends Screen> S tapAndExpect(Class<S> next) {
        WebElement element = waitVisible();
        Log.step("Tap " + name() + " -> " + next.getSimpleName(), Status.PASSED);
        long start = System.nanoTime();
        element.click();
        S screen = ScreenFactory.create(next).waitReady();
        PerfCollector.transition(owner, field, screen.name(), (System.nanoTime() - start) / 1_000_000);
        return screen;
    }

    /** Replaces the content of an input. Fields whose name contains "password" are masked in the report. */
    public UiElement type(CharSequence text) {
        WebElement element = waitVisible();
        String shown = field.toLowerCase(Locale.ROOT).contains("password") ? "*****" : String.valueOf(text);
        Log.step("Type '" + shown + "' into " + name(), Status.PASSED);
        element.clear();
        element.sendKeys(text);
        return this;
    }

    public UiElement clear() {
        waitVisible().clear();
        return this;
    }

    public String text() {
        return waitVisible().getText();
    }

    public String attribute(String name) {
        return waitVisible().getAttribute(name);
    }

    /** Swipes (up to {@code maxSwipes}) until the element is visible. */
    public UiElement scrollIntoView(Gestures gestures, int maxSwipes) {
        if (!gestures.scrollUntil(this::isDisplayed, maxSwipes)) {
            throw new ElementNotFoundException(name() + " did not appear after " + maxSwipes + " swipes [" + by + "]",
                    null);
        }
        return this;
    }

    UiElement nth(int i) {
        return new UiElement(owner, field, by, context, i);
    }

    Supplier<? extends SearchContext> context() {
        return context;
    }

    String owner() {
        return owner;
    }

    String field() {
        return field;
    }

    private static Duration poll() {
        return TafConfig.get().duration("wait.poll");
    }

    @Override
    public String toString() {
        return name() + " [" + by + "]";
    }
}
