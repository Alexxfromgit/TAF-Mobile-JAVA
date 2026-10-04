package io.github.alexxfromgit.taf.mobile.core.screen;

/**
 * Base for mixin interfaces that share UI between screens without inheritance chains:
 * <pre>{@code
 * public interface HasHeader extends ScreenMixin {
 *     default Header header() { return component(Header.class); }
 * }
 * public class CatalogScreen extends Screen implements HasHeader { ... }
 * }</pre>
 */
public interface ScreenMixin {

    <C extends Component> C component(Class<C> type);
}
