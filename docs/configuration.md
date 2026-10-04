# Configuration

## Layers

Later layers win:

| # | Layer | Typical use |
|---|---|---|
| 1 | `taf/defaults.properties` (inside taf-core) | Framework defaults (below) |
| 2 | `src/test/resources/taf.properties` | App ids, users, shared capabilities |
| 3 | `src/test/resources/env/<env>.properties` | Target, device, app path. Select with `-Denv=` or `TAF_ENV`. |
| 4 | System properties | `-Dcaps.ios.platformVersion=18.5` |
| 5 | Environment variables | `APPIUM_URL=...` overrides `appium.url` (only keys declared in a file) |
| 6 | Runtime overrides | Values set during the run |

The environment is selected by the system property `taf.env` (or the `TAF_ENV` environment variable). The examples
POM maps the Maven property `-Denv=<name>` to it, so `./mvnw verify -Denv=staging` works from the repository root
without affecting taf-core's own unit tests.

Values may reference other keys: `app.path=${apps.dir}/app.apk`.

## Secrets

Read only from environment variables (or `-D` for local runs), never from files: user passwords
(`USERS_<ALIAS>_PASSWORD`) and cloud credentials (`CLOUD_USERNAME`, `CLOUD_ACCESS_KEY`). Loading fails if a
`.properties` file contains a literal value for a secret-looking key, and the linter checks the same.

## Reference

| Key | Default | |
|---|---|---|
| `platform` | `android` | `android` / `ios` |
| `target` | `local` | `local` / `managed` / `cloud` |
| `appium.url` | `http://127.0.0.1:4723` | `local` target |
| `appium.args` | | Extra server arguments for `managed` |
| `appium.new-command-timeout` | `120s` | |
| `app.path` | | App file (relative to the examples module), absolute path or URL |
| `app.id.android` / `app.id.ios` | | Needed for restart/reset between tests |
| `caps.*`, `caps.android.*`, `caps.ios.*` | | Appium capabilities (typed) |
| `cloud.provider` / `cloud.url` / `cloud.app` / `cloud.options.*` | | Device clouds |
| `devices` | | Comma-separated udids for parallel runs |
| `device.lease-timeout` | `10m` | Wait for a free device |
| `device.android.system-port-base` / `device.ios.wda-port-base` / `device.ios.mjpeg-port-base` | `8200` / `8100` / `9100` | Per-device ports = base + index |
| `wait.timeout` / `wait.poll` / `wait.screen-timeout` | `10s` / `250ms` / `20s` | Explicit waits |
| `session.default` / `session.after-failure` | `RESTART_APP` / `NEW_SESSION` | See [sessions.md](sessions.md) |
| `users.<alias>.username` | | `@TestUser` (password from `USERS_<ALIAS>_PASSWORD`) |
| `artifacts.screenshot-on-failure` / `artifacts.page-source-on-failure` | `true` | |
| `artifacts.video` / `artifacts.video.max-duration` | `off` / `5m` | `off` / `on-failure` / `always` |
| `perf.enabled` / `perf.threshold.lookup` / `perf.threshold.transition` / `perf.fail` | `true` / `1s` / `3s` / `false` | |
| `verify.mode` | `hard` | `soft`: collect `Verify` failures (with screenshots) |
| `retry.max` / `retry.on` | `1` / infrastructure exceptions | Assertions are never retried |
| `lint.*` | `true` | `require-owner`, `require-feature`, `secrets`, `screens`; `quarantine.max-days=90` |

## Build properties (examples POM)

| Property | Default | |
|---|---|---|
| `env` | `android-local` | Environment file |
| `suite` | `suites/static.xml` | `-Pe2e` switches to `suites/regression.xml` |
| `taf.project-dir` | the examples module | Base for relative `app.path` |
