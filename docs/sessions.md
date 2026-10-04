# Sessions, users and preconditions

Creating an Appium session takes seconds to minutes. Restarting the app takes about a second. `MobileTestBase`
picks the cheapest start that is still safe, before every test.

## Start modes

| Mode | What happens | Typical cost |
|---|---|---|
| `NEW_SESSION` | Quit the session, start a new one | slowest |
| `RESET_APP_DATA` | Terminate, wipe data (Android `mobile: clearApp`; iOS reinstall), relaunch | medium |
| `RESTART_APP` (default) | Terminate and relaunch: start screen, data kept | fast |
| `REUSE` | Continue where the previous test ended | fastest (for chained tests only) |

## Decision rules (`SessionPolicy`)

1. No session yet → `NEW_SESSION`
2. The previous test on this thread **failed** → `session.after-failure` (default `NEW_SESSION`). A failure can
   leave the app or the driver in any state.
3. `@FreshSession` on the test or class → `NEW_SESSION`
4. `@ResetAppData`, or a different `@TestUser` than the previous test → `RESET_APP_DATA`
5. Otherwise → `session.default` (default `RESTART_APP`)

Restart and reset need the app id: `app.id.android` (package) and `app.id.ios` (bundle id). Without it the
framework starts a new session instead.

## Test users

```java
@Test
@TestUser("standard")
public void ... { user().orElseThrow().username(); }
```

```properties
users.standard.username=bob@example.com   # taf.properties / env file
# USERS_STANDARD_PASSWORD=...              # environment variable or CI secret
```

`UserCredentials.toString()` never prints the password, and typing into fields named `*password*` is masked in the
report.

## Preconditions

Turn setup into annotations:

```java
@Retention(RUNTIME) @Target(METHOD)
@Precondition(handler = CartContainsHandler.class, order = 2)
public @interface CartContains { String[] value(); }

public class CartContainsHandler implements PreconditionHandler<CartContains> {
    public void apply(CartContains annotation, Context context) {
        for (String product : annotation.value()) { ... add through UI, deep link or API ... }
    }
}
```

Handlers run after the session is prepared and before the test body, ordered by `order`. Each one is a step in the
report. If a handler cannot prepare the state, throw `TestDataException`, and the test is reported as a test data
problem, not as a product defect.

Choose the fastest reliable route: a deep link (`driver().get("myapp://cart/add/42")`), a backend API call,
a launch argument, or the UI as the last resort.
