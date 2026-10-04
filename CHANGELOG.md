# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- `UiContainer.element(name, androidBy, iosBy)` for elements whose locator is only known at runtime
  (e.g. a list row by its text) - safer than list indexes in recycled lists.
- `Gestures.scrollUntil(condition, maxSwipes, direction)`, e.g. back to the top of a restored list.

### Changed
- `Gestures.scroll` drags slower and holds before releasing, so lists do not fling past the searched item.

### Fixed
- `-Denv=...` from the repository root no longer leaks into taf-core's unit tests: the framework now reads the
  `taf.env` system property (the examples POM maps `-Denv` to it).
- Failures inside `MobileTestBase.prepareSession` (preconditions) now attach a screenshot and page source.
- Retry log messages show the failure instead of a literal `{}`.
- Example locators verified on an Android 16 emulator (header root id, empty cart state, price format); the
  examples work around crash bugs of My Demo App 2.3.0 (documented in `Products`).

## [1.0.0] - 2026-10-01

### Added
- Cross-platform screen model: `@Locate` / `@Using` / `How`, lazily re-located `UiElement`s with explicit waits,
  `Component`s scoped to a root, `ComponentList`, mixin interfaces, `@ScreenIdentifier` + `@WaitFor` readiness.
- Driver layer for Android (UiAutomator2) and iOS (XCUITest): `local`, `managed` (framework-started Appium) and
  `cloud` targets (BrowserStack, Sauce Labs, LambdaTest), capabilities from configuration, device pool with
  per-device ports for parallel runs.
- Session policy (`NEW_SESSION`, `RESET_APP_DATA`, `RESTART_APP`, `REUSE`) with `@FreshSession` / `@ResetAppData`.
- `@TestUser` with credentials from configuration + environment, declarative `@Precondition` handlers.
- `MockServer`: WireMock reverse and forward proxy with passthrough, stubs and request assertions.
- Screen performance timings (lookups, screen ready, transitions) with thresholds.
- Failure evidence: screenshot, page source, optional screen recording; soft-assert screenshots.
- Shared foundation: layered config with secrets guard, failure taxonomy and Allure categories, `Verify`, `Log`,
  `@KnownIssue`, `@Quarantined`, infrastructure-only retries, severity from groups, metadata linter.
- Examples for the Sauce Labs My Demo App; CI (device-free), Android emulator workflow, manual iOS workflow.
