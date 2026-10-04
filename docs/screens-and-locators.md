# Screens, components and locators

## Screens

A screen is a class with injected, lazily located fields. Navigation methods return the next screen;
query methods return values. Tests then read as flows.

```java
public class CatalogScreen extends Screen implements HasHeader {

    @ScreenIdentifier                                   // proves the screen is open
    @Locate(value = "Catalog-screen", android = @Using("Displays all products of catalog"))
    private UiElement catalog;

    @Locate(value = "ProductItem", android = @Using(how = How.XPATH, value = "//*[@resource-id='...:id/productRV']/android.view.ViewGroup"))
    private ComponentList<ProductTile> products;

    public ProductScreen openProduct(String title) {
        return products.first(p -> p.title().equals(title)).open();
    }
}
```

`ScreenFactory.create(CatalogScreen.class)` builds the object without touching the device.
`waitReady()` waits until the screen is ready. In tests, `on(CatalogScreen.class)` does both.

### Readiness

`waitReady()`:
1. waits until every `@WaitFor(gone = ...)` element has disappeared (spinners, progress bars);
2. waits until every `@ScreenIdentifier` element is visible;
3. records the time as *Screen ready* in the performance report.

Timeout: `wait.screen-timeout` (20 s), or `@WaitFor(timeout = "30s")` per screen. If it times out, the result is
`ScreenNotReadyException: CatalogScreen is not ready after 20s: CatalogScreen.catalog is not visible ...`.

The linter fails the build for screens without a `@ScreenIdentifier`.

## Locators

```java
@Locate("Login")                                          // accessibility id, both platforms
@Locate(android = @Using(how = How.ID, value = "loginBtn"),
        ios = @Using("LoginButton"))                       // per platform
@Locate(value = "Login", android = @Using(how = How.ID, value = "loginBtn"))   // iOS uses the value
```

| `How` | Android | iOS |
|---|---|---|
| `ACCESSIBILITY_ID` (default) | content-description | accessibilityIdentifier / label |
| `ID` | resource-id (`loginBtn` or `pkg:id/loginBtn`) | name |
| `TEXT` | `UiSelector().text(...)` | `label == ... OR name == ...` |
| `CLASS_NAME`, `XPATH` | yes | yes |
| `ANDROID_UIAUTOMATOR` | yes | fails fast |
| `IOS_CLASS_CHAIN`, `IOS_PREDICATE` | fails fast | yes |

Prefer, in this order: accessibility ids (ask developers to add them, which also helps real users), resource ids,
text, class chains or UiAutomator, and XPath last.

## UiElement

Every call re-locates the element with an explicit wait (`wait.timeout`, polling `wait.poll`). Elements never go
stale and no implicit wait is involved.

| Method | |
|---|---|
| `tap()`, `type(text)`, `clear()`, `text()`, `attribute(name)` | Wait until visible, then act. Each action is an Allure step (password fields are masked). |
| `tapAndExpect(Next.class)` | Tap, then wait until `Next` is ready. The time is recorded as a transition. |
| `isDisplayed()`, `isPresent()` | Immediate checks, no waiting |
| `waitVisible()`, `waitGone()` | Explicit waits with optional timeout |
| `scrollIntoView(gestures, maxSwipes)` | Swipe until visible |

## Components

A component is a part of the UI whose elements are searched **inside its root**:

```java
@Locate(android = @Using(how = How.ID, value = "headerCL"),
        ios = @Using(how = How.CLASS_NAME, value = "XCUIElementTypeTabBar"))
public class Header extends Component {
    @Locate(value = "Cart-tab-item", android = @Using("View cart"))
    private UiElement cart;
}
```

Use one in three ways:
- as a field: `private Header header;` (the root comes from the class-level `@Locate`, or a field-level one);
- through `component(Header.class)` from any screen;
- as list items: `ComponentList<ProductTile>`, where the field's `@Locate` finds the item roots and `get(i)`,
  `first(predicate)`, `stream()` and `waitAtLeast(n)` give you scoped items.

## Mixins

Share UI between screens without inheritance chains:

```java
public interface HasHeader extends ScreenMixin {
    default Header header() { return component(Header.class); }
}
public class CartScreen extends Screen implements HasHeader { ... }
```

## Gestures

`gestures().scroll(Direction.DOWN)`, `swipe(from, to, duration)`, `tap(point)` and `scrollUntil(condition, max)`
use W3C actions, the same API on both platforms.
