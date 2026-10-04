package io.github.alexxfromgit.taf.mobile.core.screen;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

/** Explicit polling, so test code never needs implicit waits or {@code Thread.sleep}. */
public final class Waits {

    private Waits() {
    }

    /**
     * Calls {@code attempt} until it returns a value that is neither {@code null} nor {@code Boolean.FALSE},
     * or the timeout passes. "Element not found" and "stale element" exceptions count as "not yet".
     */
    public static <T> Optional<T> poll(Supplier<T> attempt, Duration timeout, Duration interval) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (true) {
            try {
                T value = attempt.get();
                if (value != null && !Boolean.FALSE.equals(value)) {
                    return Optional.of(value);
                }
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                // not there yet
            }
            if (System.nanoTime() >= deadline) {
                return Optional.empty();
            }
            try {
                Thread.sleep(interval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
    }

    static String format(Duration duration) {
        long ms = duration.toMillis();
        return ms % 1000 == 0 ? (ms / 1000) + "s" : ms + "ms";
    }
}
