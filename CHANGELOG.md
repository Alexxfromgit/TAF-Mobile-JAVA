# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Cross-platform screen model: `@Locate` / `@Using` / `How`, lazily re-located `UiElement`s with explicit waits,
  `Component`s scoped to a root, `ComponentList`, mixin interfaces, `@ScreenIdentifier` + `@WaitFor` readiness,
  and `UiContainer.element(...)` for locators only known at runtime (e.g. a list row by its text).
- Driver layer for Android (UiAutomator2) and iOS (XCUITest): `local`, `managed` (framework-started Appium) and
  `cloud` targets (BrowserStack, Sauce Labs, LambdaTest), capabilities from configuration, device pool with
  per-device ports for parallel runs.
- Session policy (`NEW_SESSION`, `RESET_APP_DATA`, `RESTART_APP`, `REUSE`) with `@FreshSession` / `@ResetAppData`.
- `@TestUser` with credentials from configuration + environment, declarative `@Precondition` handlers.
- W3C gestures: swipe, tap and fling-free scrolling, `scrollUntil(condition, maxSwipes[, direction])`.
- `MockServer`: WireMock reverse and forward proxy with passthrough, stubs and request assertions.
- Screen performance timings (lookups, screen ready, transitions) with thresholds.
- Failure evidence: screenshot and page source for failed tests and failed preconditions, optional screen
  recording, a screenshot per soft-assert failure.
- Shared foundation: layered config (environment selected with `taf.env`, `-Denv` in Maven) with secrets guard,
  failure taxonomy and Allure categories, `Verify`, `Log`, `@KnownIssue`, `@Quarantined`, infrastructure-only
  retries, severity from groups, metadata linter.
- Examples for the Sauce Labs My Demo App, verified on an Android 16 emulator; CI (device-free), Android emulator
  workflow with GitHub Pages report, manual iOS workflow.

### Known issues
- My Demo App 2.3.0 (Android) crashes when the 7th or later catalog product is opened, and opens the wrong
  product (or crashes) when a second product is opened in the same launch. The examples open one product per
  launch from the first six; see `Products` in the examples.
- iOS locators in the examples are best-effort and have not been run on a simulator yet.
