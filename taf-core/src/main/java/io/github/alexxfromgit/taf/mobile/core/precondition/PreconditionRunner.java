package io.github.alexxfromgit.taf.mobile.core.precondition;

import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import io.github.alexxfromgit.taf.mobile.core.users.UserCredentials;
import io.qameta.allure.Allure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Finds {@link Precondition}-annotated annotations on a test method and runs their handlers in order. */
public final class PreconditionRunner {

    private static final Map<Class<?>, PreconditionHandler<?>> HANDLERS = new ConcurrentHashMap<>();

    private PreconditionRunner() {
    }

    /** Preconditions declared on {@code method}, sorted by {@link Precondition#order()}. */
    public static List<Annotation> preconditionsOf(Method method) {
        return Arrays.stream(method.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(Precondition.class))
                .sorted(Comparator.comparingInt(a -> a.annotationType().getAnnotation(Precondition.class).order()))
                .toList();
    }

    public static void run(Method method, Optional<UserCredentials> user) {
        PreconditionHandler.Context context = new PreconditionHandler.Context(method, user);
        for (Annotation annotation : preconditionsOf(method)) {
            PreconditionHandler<Annotation> handler = handler(annotation);
            String name = "Precondition: @" + annotation.annotationType().getSimpleName();
            if (Allure.getLifecycle().getCurrentTestCaseOrStep().isPresent()) {
                Allure.step(name, () -> handler.apply(annotation, context));
            } else {
                handler.apply(annotation, context);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static PreconditionHandler<Annotation> handler(Annotation annotation) {
        Class<? extends PreconditionHandler<?>> type = annotation.annotationType().getAnnotation(Precondition.class).handler();
        return (PreconditionHandler<Annotation>) HANDLERS.computeIfAbsent(type, t -> {
            try {
                var constructor = t.getDeclaredConstructor();
                constructor.setAccessible(true);
                return (PreconditionHandler<?>) constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new FrameworkException("Cannot create precondition handler " + t.getName()
                        + " (needs a no-argument constructor)", e);
            }
        });
    }
}
