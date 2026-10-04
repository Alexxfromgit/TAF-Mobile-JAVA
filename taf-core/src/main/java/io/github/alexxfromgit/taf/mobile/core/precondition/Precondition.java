package io.github.alexxfromgit.taf.mobile.core.precondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation that turns your own annotation into a declarative test precondition:
 * <pre>{@code
 * @Retention(RUNTIME) @Target(METHOD)
 * @Precondition(handler = CartContainsHandler.class)
 * public @interface CartContains { String[] value(); }
 *
 * @Test @CartContains("Sauce Labs Backpack")
 * public void checkout() { ... }   // the test starts with the backpack in the cart
 * }</pre>
 * Handlers run after the session is prepared, before the test body, in ascending {@link #order()}.
 * Prepare state the cheapest way available: deep links, API calls or, as the last resort, the UI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Precondition {

    Class<? extends PreconditionHandler<?>> handler();

    int order() default 0;
}
