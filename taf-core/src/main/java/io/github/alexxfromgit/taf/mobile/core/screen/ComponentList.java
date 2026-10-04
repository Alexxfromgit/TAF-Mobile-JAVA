package io.github.alexxfromgit.taf.mobile.core.screen;

import io.github.alexxfromgit.taf.mobile.core.failure.ElementNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Repeated components, e.g. product tiles of a catalog. The field's {@link Locate} finds the item roots.
 * <pre>{@code
 * @Locate(android = @Using(how = How.XPATH, value = "//*[@resource-id='...:id/productRV']/android.view.ViewGroup"))
 * private ComponentList<ProductTile> products;
 * }</pre>
 */
public final class ComponentList<C extends Component> {

    private final Class<C> type;
    private final UiElements items;
    private final UiElement template;
    private final UiContext parentContext;

    ComponentList(Class<C> type, UiElement template, UiContext parentContext) {
        this.type = type;
        this.template = template;
        this.items = new UiElements(template);
        this.parentContext = parentContext;
    }

    /** Item at {@code index} (0-based); its elements are searched inside that item only. */
    public C get(int index) {
        return ScreenFactory.componentAt(type, template.nth(index), parentContext.platform());
    }

    /** Current number of items, without waiting. */
    public int size() {
        return items.count();
    }

    /** Waits until at least {@code minimum} items are present. */
    public ComponentList<C> waitAtLeast(int minimum) {
        items.waitAtLeast(minimum);
        return this;
    }

    public Stream<C> stream() {
        return IntStream.range(0, size()).mapToObj(this::get);
    }

    public List<C> all() {
        return stream().toList();
    }

    public Optional<C> find(Predicate<C> condition) {
        return stream().filter(condition).findFirst();
    }

    public C first(Predicate<C> condition) {
        return find(condition).orElseThrow(() -> new ElementNotFoundException(
                "No item of " + template.name() + " matches the condition (" + size() + " items checked)", null));
    }
}
