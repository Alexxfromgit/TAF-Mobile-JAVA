package io.github.alexxfromgit.taf.mobile.core.screen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Where to find a field's element. One accessibility id for both platforms, or a locator per platform:
 * <pre>{@code
 * @Locate("test-Login") private UiElement login;
 *
 * @Locate(android = @Using(how = How.ID, value = "loginBtn"),
 *         ios     = @Using("LoginButton"))
 * private UiElement login;
 * }</pre>
 * On a {@link Component} class it declares the component's root element.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Locate {

    /** Accessibility id used on every platform without its own locator. */
    String value() default "";

    Using android() default @Using(how = How.UNSET, value = "");

    Using ios() default @Using(how = How.UNSET, value = "");
}
