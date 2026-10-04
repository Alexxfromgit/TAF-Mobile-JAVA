# Reports, failure taxonomy and suite hygiene

## Failure taxonomy

Allure categories (`taf/allure/categories.json`, overridable with `src/test/resources/allure/categories.json`)
tell you who has to act:

| Exception | Status | Category | Meaning |
|---|---|---|---|
| `ElementNotFoundException`, `ScreenNotReadyException` | failed | UI element not found | The app changed or a locator is outdated (message names field + locator) |
| `PotentialDefectException` / any `AssertionError` | failed | Product defects | The app behaves differently from the expectation |
| `KnownIssueException` | failed | Known issues | Wrapped automatically for `@KnownIssue` tests |
| `TestDataException` | broken | Test data problems | A precondition could not be prepared |
| `EnvironmentException`, `SessionNotCreatedException`, connection errors | broken | Environment / infrastructure | No Appium, no device, backend down |
| `FrameworkException` | broken | Framework problems | Misconfiguration or a bug in test code |

## Failure evidence

For every failed test or fixture: **Screenshot on failure** and **Page source on failure** (the UI hierarchy XML,
which shows why a locator did not match). With `artifacts.video=on-failure`, a screen recording is attached too.
In soft mode, every failed `Verify` gets its own screenshot.

## Verify and Log

```java
Verify.equal(cart.total(), "$29.99", "Cart total");
Verify.that(menu.isLoggedIn(), "Menu offers 'Log Out'");
Verify.softly().assertThat(cart.itemTitles()).contains(BACKPACK);   // always soft
Log.info("Order total: {}", total);                                  // log + Allure step
```

UI actions (`tap`, `type`, ...) are steps automatically, so a report reads like a test script.

## Retries, known issues, quarantine

- `InfraRetryAnalyzer` retries only infrastructure failures (`retry.on`), never assertions or missing elements.
  After a failure the next test on the thread gets a fresh session (`session.after-failure`).
- `@KnownIssue(id = "42")`: the test runs; a failure lands in *Known issues* with a link.
- `@Quarantined(until = "2026-11-15", reason = "...")`: disabled until that date, then it runs again automatically.
  The linter limits quarantine to 90 days.

## Metadata linter

The first `<test>` of every suite, with no device needed, fails when:
- a test has no `@Owner` or no `@Epic`/`@Feature`/`@Story`;
- a screen has no `@ScreenIdentifier`;
- a quarantine is invalid or too long;
- a `.properties` file contains a secret.

## Severity from groups

The groups `blocker`, `critical` and `minor` (`Groups`) set the Allure severity unless `@Severity` is present.

## Framework reports

`MobileReports` (last `<test>`) attaches the [screen performance](performance.md) report. The files are also
written to `examples/target/taf-reports/`.
