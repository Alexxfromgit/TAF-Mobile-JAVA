package io.github.alexxfromgit.taf.mobile.core.screen;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;
import io.github.alexxfromgit.taf.mobile.core.failure.ElementNotFoundException;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

/** All elements matching one locator, e.g. rows of a list. Use {@link ComponentList} for structured items. */
public final class UiElements {

    private final UiElement template;

    UiElements(UiElement template) {
        this.template = template;
    }

    /** Current number of matches, without waiting. */
    public int count() {
        try {
            return template.context().get().findElements(template.by()).size();
        } catch (RuntimeException | ElementNotFoundException e) {
            return 0;
        }
    }

    public UiElement get(int index) {
        return template.nth(index);
    }

    /** Snapshot of the matches visible right now. */
    public List<UiElement> all() {
        return IntStream.range(0, count()).mapToObj(template::nth).toList();
    }

    public List<String> texts() {
        return all().stream().map(UiElement::text).toList();
    }

    /** Waits until at least {@code minimum} elements match. */
    public UiElements waitAtLeast(int minimum) {
        Duration timeout = TafConfig.get().duration("wait.timeout");
        if (Waits.poll(() -> count() >= minimum, timeout, TafConfig.get().duration("wait.poll")).isEmpty()) {
            throw new ElementNotFoundException(template.name() + ": expected at least " + minimum
                    + " elements after " + Waits.format(timeout) + " but found " + count(), null);
        }
        return this;
    }
}
