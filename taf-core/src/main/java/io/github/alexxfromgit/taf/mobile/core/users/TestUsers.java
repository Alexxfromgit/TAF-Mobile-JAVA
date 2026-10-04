package io.github.alexxfromgit.taf.mobile.core.users;

import io.github.alexxfromgit.taf.mobile.core.config.TafConfig;

import java.lang.reflect.Method;
import java.util.Optional;

/** Resolves {@link TestUser} aliases to credentials (username from config, password from a secret). */
public final class TestUsers {

    private TestUsers() {
    }

    public static UserCredentials get(String alias) {
        TafConfig config = TafConfig.get();
        return new UserCredentials(alias,
                config.string("users." + alias + ".username"),
                config.secret("users." + alias + ".password"));
    }

    /** Alias declared on the method or its class, if any. */
    public static Optional<String> aliasOf(Method method) {
        TestUser user = method.isAnnotationPresent(TestUser.class)
                ? method.getAnnotation(TestUser.class)
                : method.getDeclaringClass().getAnnotation(TestUser.class);
        return Optional.ofNullable(user).map(TestUser::value);
    }
}
