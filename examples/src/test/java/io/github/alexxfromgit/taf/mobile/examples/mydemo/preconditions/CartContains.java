package io.github.alexxfromgit.taf.mobile.examples.mydemo.preconditions;

import io.github.alexxfromgit.taf.mobile.core.precondition.Precondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The test starts with these products in the cart (one of each), back on the catalog. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Precondition(handler = CartContainsHandler.class, order = 2)
public @interface CartContains {

    /** Product titles, e.g. {@code Products.BACKPACK} (one product with the demo app, see {@code Products}). */
    String[] value();
}
