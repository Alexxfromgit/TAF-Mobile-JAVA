package io.github.alexxfromgit.taf.mobile.core.gesture;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;

/** Touch gestures with W3C actions (no deprecated TouchAction), identical on Android and iOS. */
public final class Gestures {

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    /** Slow enough and with a final hold, so lists move by the dragged distance and do not fling past items. */
    private static final Duration SCROLL_DURATION = Duration.ofMillis(700);
    private static final Duration HOLD_BEFORE_RELEASE = Duration.ofMillis(250);

    private final AppiumDriver driver;

    public Gestures(AppiumDriver driver) {
        this.driver = driver;
    }

    /** A fast swipe (may fling scrollable content). For controlled scrolling use {@link #scroll(Direction)}. */
    public void swipe(Point from, Point to, Duration duration) {
        drag(from, to, duration, Duration.ZERO);
    }

    private void drag(Point from, Point to, Duration duration, Duration hold) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), from.getX(), from.getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), to.getX(), to.getY()));
        if (!hold.isZero()) {
            swipe.addAction(new Pause(finger, hold));
        }
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipe));
    }

    /**
     * Scrolls the content by about a third of the screen in {@code direction} (DOWN = reveal content below),
     * without fling, so nothing is skipped between two checks of {@link #scrollUntil}.
     */
    public void scroll(Direction direction) {
        Dimension size = driver.manage().window().getSize();
        int cx = size.getWidth() / 2;
        int cy = size.getHeight() / 2;
        int dy = (int) (size.getHeight() * 0.18);
        int dx = (int) (size.getWidth() * 0.3);
        switch (direction) {
            case DOWN -> drag(new Point(cx, cy + dy), new Point(cx, cy - dy), SCROLL_DURATION, HOLD_BEFORE_RELEASE);
            case UP -> drag(new Point(cx, cy - dy), new Point(cx, cy + dy), SCROLL_DURATION, HOLD_BEFORE_RELEASE);
            case RIGHT -> drag(new Point(cx + dx, cy), new Point(cx - dx, cy), SCROLL_DURATION, HOLD_BEFORE_RELEASE);
            case LEFT -> drag(new Point(cx - dx, cy), new Point(cx + dx, cy), SCROLL_DURATION, HOLD_BEFORE_RELEASE);
        }
    }

    /** Scrolls down until {@code condition} holds; returns whether it did within {@code maxSwipes}. */
    public boolean scrollUntil(BooleanSupplier condition, int maxSwipes) {
        for (int i = 0; i <= maxSwipes; i++) {
            if (condition.getAsBoolean()) {
                return true;
            }
            if (i < maxSwipes) {
                scroll(Direction.DOWN);
            }
        }
        return false;
    }

    /**
     * Scrolls in {@code direction} until {@code condition} holds, e.g. back to the top of a list whose scroll
     * position the app restored: {@code scrollUntil(header::isDisplayed, 10, Direction.UP)}.
     */
    public boolean scrollUntil(BooleanSupplier condition, int maxSwipes, Direction direction) {
        for (int i = 0; i <= maxSwipes; i++) {
            if (condition.getAsBoolean()) {
                return true;
            }
            if (i < maxSwipes) {
                scroll(direction);
            }
        }
        return false;
    }

    public void tap(Point point) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), point.getX(), point.getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }
}
