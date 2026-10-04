package io.github.alexxfromgit.taf.mobile.core.screen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares how a screen becomes ready, on top of its {@link ScreenIdentifier} fields:
 * <pre>{@code
 * @WaitFor(gone = @Locate(android = @Using(how = How.CLASS_NAME, value = "android.widget.ProgressBar")),
 *          timeout = "30s")
 * public class SearchResultsScreen extends Screen { ... }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WaitFor {

    /** Elements that must disappear first (spinners, progress bars, overlays). */
    Locate[] gone() default {};

    /** Whether to wait for the {@link ScreenIdentifier} fields to be visible. */
    boolean identifiers() default true;

    /** Overrides {@code wait.screen-timeout}, e.g. {@code "30s"}. */
    String timeout() default "";
}
