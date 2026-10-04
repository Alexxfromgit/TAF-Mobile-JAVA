# Contributing

Thanks for helping! Bug reports, docs fixes and features are all welcome.

## Build

```bash
./mvnw verify                                              # no device needed
./mvnw verify -Denv=android-local -Dsuite=suites/smoke.xml # with an emulator, see docs/local-setup.md
```

JDK 21+ is required. CI uses Temurin 21.

## Guidelines

- **Framework code goes into `taf-core`** and must not know about the example app. Every behaviour change needs a
  unit test that runs **without a device** (Mockito drivers, embedded WireMock).
- Keep the framework free of shared mutable state: tests run in parallel, one session per thread.
- New locator strategies, cloud providers or start modes need docs in `docs/` and a line in `CHANGELOG.md`.
- Prefer configuration keys with sensible defaults over new mandatory setup. Document new keys in
  `docs/configuration.md` and `taf/defaults.properties`.
- Never commit app binaries, secrets, internal hostnames or real personal data.
- Commit messages: imperative mood, short subject line (`Add LambdaTest provider`).

## Pull requests

1. Open an issue first for larger changes, so we can agree on the approach.
2. Keep PRs focused and update `CHANGELOG.md` under *Unreleased*.
3. Make sure `./mvnw verify` is green. If you touched the examples, also run the smoke suite on an emulator.

By contributing you agree that your contributions are licensed under the MIT License.
