# mobile-taf

[![CI](https://github.com/Alexxfromgit/TAF-Mobile-JAVA/actions/workflows/ci.yml/badge.svg)](https://github.com/Alexxfromgit/TAF-Mobile-JAVA/actions/workflows/ci.yml)
[![Android E2E](https://github.com/Alexxfromgit/TAF-Mobile-JAVA/actions/workflows/android-e2e.yml/badge.svg)](https://github.com/Alexxfromgit/TAF-Mobile-JAVA/actions/workflows/android-e2e.yml)
[![Allure report](https://img.shields.io/badge/report-Allure-orange)](https://alexxfromgit.github.io/TAF-Mobile-JAVA/)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Java 21](https://img.shields.io/badge/java-21-informational)

**A ready-to-use Java framework for mobile UI test automation with Appium.** Click **Use this template**, point it
at your app, and you have cross-platform screen objects, smart session handling, network mocking, screen
performance timings, Allure reporting and CI on an Android emulator.

It reimplements, from scratch, patterns proven in a large production test suite, on a current stack: Java 21,
TestNG, Appium 3 with java-client 10 (UiAutomator2 / XCUITest), WireMock 3 and Allure 2.

```java
@Test(groups = Groups.SMOKE)
@TestUser("standard")
@LoggedIn
@CartContains(Products.BACKPACK)
public void loggedInUserCompletesCheckout() {
    CheckoutCompleteScreen complete = on(CatalogScreen.class)
            .header().openCart()
            .proceedToCheckout()
            .enter(Address.sample())
            .enter(PaymentCard.testVisa())
            .placeOrder();

    Verify.that(complete.isOpen(), "Checkout complete screen is shown");
}
```

Preconditions are declarative, navigation is fluent, and every screen transition is timed. If the test fails,
the report shows the screenshot, the UI hierarchy and, optionally, a video.

## Features

| | |
|---|---|
| **Declarative, cross-platform screens** | `@Locate("id")` for both platforms, or `@Locate(android = @Using(...), ios = @Using(...))`. Components (`Header`, list items) are scoped to their root. Behaviour is shared through mixin interfaces. |
| **Readable failures** | `CatalogScreen.sortButton is not visible after 10s [AppiumBy.id: sortIV]`: screen, field and locator in one line. |
| **Screen readiness** | `@ScreenIdentifier` + `@WaitFor(gone = spinner)`: `waitReady()` knows when a screen is really open. Explicit waits only, no `Thread.sleep`. |
| **Smart sessions** | The cheapest safe start for every test: restart the app, wipe its data, or start a new session after a failure or user switch. Parallel runs get a device pool with per-device ports. |
| **Android, iOS, clouds** | `target=local \| managed \| cloud`. The framework can start Appium itself. BrowserStack, Sauce Labs and LambdaTest are configured purely by properties. |
| **Declarative preconditions** | `@LoggedIn`, `@CartContains(...)`: your annotations, your handlers, run before the test body. `@TestUser("standard")` reads credentials from config and env vars. |
| **Network mocking** | WireMock in reverse- or forward-proxy mode. Everything passes through to the real backend except what a test stubs. Assert the requests your app sent. |
| **Screen performance** | Element lookups, screen-ready times and tap-to-next-screen transitions with min/avg/p95/max and thresholds, collected for free during functional tests. |
| **Failure evidence** | Screenshot + page source on every failure, a screenshot per soft-assert failure, and optional screen recording (Android and iOS). |
| **Suite hygiene** | Allure categories (*UI element not found*, *Product defects*, *Environment*...), infra-only retries, `@KnownIssue`, `@Quarantined`, and a linter that fails on screens without identifiers or tests without owner. |

## Quick start

Without a device, just JDK 21+:

```bash
./mvnw verify          # framework unit tests + static checks of the example tests
```

With an Android emulator (setup guide: [docs/local-setup.md](docs/local-setup.md)):

```bash
./scripts/download-apps.sh android     # or scripts/download-apps.ps1 on Windows
appium                                  # in another terminal
./mvnw verify -Denv=android-local -Dsuite=suites/smoke.xml
./mvnw -pl examples allure:serve
```

| Command | What it does |
|---|---|
| `-Denv=android-managed` | The framework starts and stops Appium itself |
| `-Denv=ios-local` | iOS simulator (macOS) |
| `-Denv=browserstack-android` | Device cloud (credentials in `CLOUD_USERNAME` / `CLOUD_ACCESS_KEY`) |
| `-Pe2e` | Full regression suite |
| `-Dartifacts.video=on-failure` | Attach a screen recording to failed tests |
| `-Dverify.mode=soft` | Collect all `Verify` failures of a test, each with a screenshot |

## Project layout

```
taf-core/   framework + unit tests (no knowledge of any app; tests run without devices)
examples/   screens, preconditions and tests for the Sauce Labs "My Demo App" (replace with your app)
scripts/    download-apps.sh / .ps1
docs/       guides
```

## Documentation

- [Adapting the template to your app](docs/adapting-to-your-app.md): start here
- [Local setup: Appium, Android emulator, iOS simulator](docs/local-setup.md)
- [Screens, components and locators](docs/screens-and-locators.md)
- [Drivers, devices and clouds](docs/drivers-and-clouds.md)
- [Sessions, users and preconditions](docs/sessions.md)
- [Network mocking](docs/network-mocking.md)
- [Screen performance](docs/performance.md)
- [Configuration reference](docs/configuration.md)
- [Reports, failure taxonomy and suite hygiene](docs/reports.md)
- [Architecture](docs/architecture.md)

## About the demo app

The examples test the open-source [Sauce Labs My Demo App](https://github.com/saucelabs/my-demo-app-android)
(Android) and its [iOS counterpart](https://github.com/saucelabs/my-demo-app-ios). The binaries are not part of
this repository: `scripts/download-apps.*` fetches them from the official releases. Android locators were taken
from the app sources and run in CI. iOS locators are best-effort, so verify them with Appium Inspector before
relying on them.

## Companion project

[TAF-Contract-Tests](https://github.com/Alexxfromgit/TAF-Contract-Tests) uses the same foundation (config, failure taxonomy,
Allure categories, linter) for API contract testing: JSON Schema, OpenAPI, drift detection and API coverage.

## Contributing and license

Contributions are welcome, see [CONTRIBUTING.md](CONTRIBUTING.md). The project is licensed under [MIT](LICENSE).
