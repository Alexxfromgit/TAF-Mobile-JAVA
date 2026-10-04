package io.github.alexxfromgit.taf.mobile.core.session;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Start this test in a brand-new Appium session (e.g. first-launch onboarding, permission dialogs). */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FreshSession {
}
