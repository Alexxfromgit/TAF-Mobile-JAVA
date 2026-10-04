package io.github.alexxfromgit.taf.mobile.core.precondition;

import org.testng.annotations.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PreconditionRunnerTest {

    static final List<String> CALLS = new ArrayList<>();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Precondition(handler = LoggedInHandler.class, order = 1)
    @interface LoggedIn {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Precondition(handler = CartHandler.class, order = 2)
    @interface CartContains {
        String[] value();
    }

    public static class LoggedInHandler implements PreconditionHandler<LoggedIn> {
        @Override
        public void apply(LoggedIn annotation, Context context) {
            CALLS.add("login");
        }
    }

    public static class CartHandler implements PreconditionHandler<CartContains> {
        @Override
        public void apply(CartContains annotation, Context context) {
            CALLS.add("cart:" + String.join(",", annotation.value()));
        }
    }

    @CartContains({"Backpack", "Bike Light"})
    @LoggedIn
    void sample() {
    }

    @Test
    public void handlersRunInDeclaredOrder() throws NoSuchMethodException {
        CALLS.clear();
        PreconditionRunner.run(getClass().getDeclaredMethod("sample"), Optional.empty());

        assertThat(CALLS).containsExactly("login", "cart:Backpack,Bike Light");
    }
}
