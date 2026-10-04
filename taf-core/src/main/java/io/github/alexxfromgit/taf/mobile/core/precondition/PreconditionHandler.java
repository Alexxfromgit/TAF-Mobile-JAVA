package io.github.alexxfromgit.taf.mobile.core.precondition;

import io.github.alexxfromgit.taf.mobile.core.users.UserCredentials;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Brings the app into the state described by annotation {@code A}. Needs a public no-argument constructor.
 * Throw {@code TestDataException} when the state cannot be prepared: the test is then reported as a
 * test data problem, not as a product defect.
 */
@FunctionalInterface
public interface PreconditionHandler<A extends Annotation> {

    void apply(A annotation, Context context);

    /** What the handler knows about the test it prepares. */
    record Context(Method testMethod, Optional<UserCredentials> user) {
    }
}
