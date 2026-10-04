# Drivers, devices and clouds

## Targets

| `target` | Appium server | Use for |
|---|---|---|
| `local` (default) | You start it (`appium`) at `appium.url` | Local development, CI with a background Appium |
| `managed` | The framework starts one server per device (`appium` must be on `PATH`) and stops them at the end | Parallel local runs, no manual steps |
| `cloud` | `cloud.provider` = `browserstack`, `saucelabs` or `lambdatest` | Real devices and many OS versions |

## Capabilities

Options are built with `UiAutomator2Options` / `XCUITestOptions` and then extended from configuration:

```properties
app.path=apps/my-app.apk                     # relative to the examples module, absolute, or an http(s) URL
caps.deviceName=Android Emulator             # every platform; "appium:" is added automatically
caps.android.autoGrantPermissions=true       # Android only (wins over caps.*)
caps.ios.platformVersion=18.5                # iOS only
appium.new-command-timeout=120s
```

`true`/`false` and integers are typed. Everything else is a string. An implicit wait is never set: the framework
uses explicit waits only.

## Parallel runs and the device pool

```properties
devices=emulator-5554,emulator-5556
```

Each thread leases a device for its session, and other threads wait (`device.lease-timeout`). Every device gets
its own ports: UiAutomator2 `systemPort` (`8200 + index`), WebDriverAgent `wdaLocalPort` (`8100 + index`) and
`mjpegServerPort` (`9100 + index`). Raise `thread-count` in the suite to the number of devices.

With no `devices` configured there is a single anonymous slot, so parallel threads queue instead of fighting over
one device.

## Clouds

```properties
target=cloud
cloud.provider=browserstack
cloud.app=bs://<uploaded app id>             # overrides app.path
cloud.options.deviceName=Google Pixel 8      # everything under cloud.options.* goes into the vendor block
cloud.options.osVersion=14.0
# cloud.url=...                              # optional, e.g. another Sauce Labs region
```

Credentials come only from the environment: `CLOUD_USERNAME` and `CLOUD_ACCESS_KEY`. They are put into
`bstack:options` / `sauce:options` / `LT:Options`, depending on the provider. Ready-made files are in
`examples/src/test/resources/env/*-android.properties`.

## Sessions

`DriverManager` keeps one session per thread and closes all of them at the end of the run.
[Sessions](sessions.md) explains how a session is reused between tests.

If a session cannot be created (no Appium server, driver not installed, device busy), the failure is an
`EnvironmentException` with a hint. It is categorised as *Environment / infrastructure* and retried once.
