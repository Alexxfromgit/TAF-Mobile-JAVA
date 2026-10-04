package io.github.alexxfromgit.taf.mobile.core.session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Start this test with wiped app data (logged out, empty cart) but keep the Appium session. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ResetAppData {
}
