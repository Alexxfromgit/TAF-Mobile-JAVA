package io.github.alexxfromgit.taf.mobile.core.screen;

import io.github.alexxfromgit.taf.mobile.core.driver.Platform;
import io.github.alexxfromgit.taf.mobile.core.failure.FrameworkException;
import org.openqa.selenium.By;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Creates screens and components and injects their {@link Locate}-annotated fields:
 * {@link UiElement}, {@link UiElements}, {@link Component} subclasses and {@link ComponentList}.
 * Creation is cheap (nothing is looked up until used), so create screens freely.
 */
public final class ScreenFactory {

    private ScreenFactory() {
    }

    /** A screen bound to the current thread's Appium session. */
    public static <S extends Screen> S create(Class<S> type) {
        return create(type, UiContext.ofSession());
    }

    /** A screen bound to an explicit context (useful in framework unit tests). */
    public static <S extends Screen> S create(Class<S> type, UiContext context) {
        S screen = instantiate(type);
        screen.init(type.getSimpleName(), context);
        inject(screen, context);
        return screen;
    }

    /** A component located by its class-level {@link Locate} inside {@code parent}. */
    static <C extends Component> C component(Class<C> type, UiContainer parent) {
        Locate locate = type.getAnnotation(Locate.class);
        if (locate == null) {
            throw new FrameworkException(type.getSimpleName() + " needs a class-level @Locate to be used with "
                    + "component(...). Alternatively declare it as a @Locate-annotated field.");
        }
        String field = decapitalize(type.getSimpleName());
        By by = LocatorResolver.resolve(locate, parent.platform(), parent.name() + "." + field);
        return componentAt(type, new UiElement(parent.name(), field, by, parent.context().searchContext()),
                parent.platform());
    }

    static <C extends Component> C componentAt(Class<C> type, UiElement root, Platform platform) {
        C component = instantiate(type);
        UiContext context = new UiContext(root::waitVisible, platform);
        component.init(root.name(), context);
        component.root(root);
        inject(component, context);
        return component;
    }

    private static void inject(UiContainer container, UiContext context) {
        for (Class<?> type = container.getClass();
             type != Screen.class && type != Component.class && type != UiContainer.class && type != Object.class;
             type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !isInjectable(field.getType())) {
                    continue;
                }
                set(container, field, valueFor(container, field, context));
            }
        }
    }

    private static Object valueFor(UiContainer container, Field field, UiContext context) {
        String where = container.name() + "." + field.getName();
        Class<?> fieldType = field.getType();
        Locate locate = field.getAnnotation(Locate.class);
        if (locate == null && Component.class.isAssignableFrom(fieldType)) {
            locate = fieldType.getAnnotation(Locate.class);
        }
        if (locate == null) {
            throw new FrameworkException(where + " needs @Locate");
        }
        By by = LocatorResolver.resolve(locate, context.platform(), where);
        UiElement element = new UiElement(container.name(), field.getName(), by, context.searchContext());

        if (fieldType == UiElement.class) {
            return element;
        }
        if (fieldType == UiElements.class) {
            return new UiElements(element);
        }
        if (fieldType == ComponentList.class) {
            return new ComponentList<>(itemType(field, where), element, context);
        }
        return componentAt(fieldType.asSubclass(Component.class), element, context.platform());
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Component> itemType(Field field, String where) {
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType parameterized
                && parameterized.getActualTypeArguments()[0] instanceof Class<?> item
                && Component.class.isAssignableFrom(item)) {
            return (Class<? extends Component>) item;
        }
        throw new FrameworkException(where + " must be declared as ComponentList<SomeComponent>");
    }

    private static boolean isInjectable(Class<?> type) {
        return type == UiElement.class || type == UiElements.class || type == ComponentList.class
                || Component.class.isAssignableFrom(type);
    }

    private static <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new FrameworkException(type.getSimpleName() + " needs a no-argument constructor", e);
        } catch (ReflectiveOperationException e) {
            throw new FrameworkException("Cannot create " + type.getSimpleName(), e);
        }
    }

    private static void set(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new FrameworkException("Cannot inject " + field, e);
        }
    }

    private static String decapitalize(String name) {
        return name.isEmpty() ? name : Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
