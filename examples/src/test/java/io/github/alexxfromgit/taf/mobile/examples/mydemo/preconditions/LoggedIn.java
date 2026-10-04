package io.github.alexxfromgit.taf.mobile.examples.mydemo.preconditions;

import io.github.alexxfromgit.taf.mobile.core.precondition.Precondition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The test starts logged in as its {@code @TestUser}, on the catalog. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Precondition(handler = LoggedInHandler.class, order = 1)
public @interface LoggedIn {
}
