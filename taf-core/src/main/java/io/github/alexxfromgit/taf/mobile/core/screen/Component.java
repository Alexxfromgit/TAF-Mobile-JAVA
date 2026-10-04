package io.github.alexxfromgit.taf.mobile.core.screen;

/**
 * A reusable piece of UI (header, list item, dialog) whose elements are searched inside its root element.
 * The root comes from the {@link Locate} on the field that declares the component, or from a {@link Locate} on
 * the component class itself (needed for {@code component(Header.class)} and mixins).
 * <pre>{@code
 * @Locate(android = @Using(how = How.ID, value = "headerCL"), ios = @Using(how = How.CLASS_NAME, value = "XCUIElementTypeNavigationBar"))
 * public class Header extends Component {
 *     @Locate("View cart") private UiElement cart;
 * }
 * }</pre>
 */
public abstract class Component extends UiContainer {

    private UiElement root;

    final void root(UiElement root) {
        this.root = root;
    }

    public UiElement root() {
        return root;
    }

    public boolean isDisplayed() {
        return root.isDisplayed();
    }
}
