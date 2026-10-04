package io.github.alexxfromgit.taf.mobile.core.screen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks elements that prove the screen is open. {@code waitReady()} waits until all of them are visible.
 * Every screen needs at least one (the metadata linter checks it).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ScreenIdentifier {
}
