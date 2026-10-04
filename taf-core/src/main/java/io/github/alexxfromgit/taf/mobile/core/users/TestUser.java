package io.github.alexxfromgit.taf.mobile.core.users;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The account a test runs as. Resolved from configuration:
 * <pre>
 * users.standard.username=bob@example.com      # in taf.properties / env file
 * USERS_STANDARD_PASSWORD=...                   # environment variable (secret)
 * </pre>
 * Switching users between tests triggers {@code RESET_APP_DATA}, so a test never inherits another user's state.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TestUser {

    /** Alias, e.g. {@code "standard"}, {@code "locked"}, {@code "admin"}. */
    String value();
}
