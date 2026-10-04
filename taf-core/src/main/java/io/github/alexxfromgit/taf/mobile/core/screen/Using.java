package io.github.alexxfromgit.taf.mobile.core.screen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A platform-specific locator inside {@link Locate}. */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Using {

    How how() default How.ACCESSIBILITY_ID;

    String value();
}
