# Local setup

## Always needed

- JDK 21+ (`java -version`). Maven comes with the wrapper (`./mvnw`).
- Node.js 20+ (`node -v`) for the Appium server.

```bash
npm install -g appium
appium driver install uiautomator2      # Android
appium driver install xcuitest          # iOS (macOS only)
appium driver doctor uiautomator2       # checks ANDROID_HOME, JAVA_HOME, adb, ...
```

## Android emulator

1. Install [Android Studio](https://developer.android.com/studio) (or only the command-line tools).
2. In the SDK Manager install *Android SDK Platform-Tools*, *Android Emulator* and a system image, for example
   *Android 14 (API 34), Google APIs, x86_64* (ARM64 on Apple Silicon).
3. Set the environment variables (adjust the path):
   - Windows (PowerShell): `setx ANDROID_HOME "$env:LOCALAPPDATA\Android\Sdk"` and add
     `%ANDROID_HOME%\platform-tools` and `%ANDROID_HOME%\emulator` to `PATH`
   - macOS/Linux: `export ANDROID_HOME=$HOME/Library/Android/sdk` (macOS) or `$HOME/Android/Sdk` (Linux) and
     `export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator`
4. Create and start an emulator from *Device Manager*, or from the command line:
   ```bash
   avdmanager create avd -n pixel_api34 -k "system-images;android-34;google_apis;x86_64" -d pixel_6
   emulator -avd pixel_api34
   adb devices          # should list emulator-5554
   ```

## Run the examples

```bash
./scripts/download-apps.sh android            # Windows: ./scripts/download-apps.ps1 -Platform android
appium                                        # keep it running in a second terminal
./mvnw verify -Denv=android-local -Dsuite=suites/smoke.xml
./mvnw -pl examples allure:serve
```

Or let the framework start Appium for you: `-Denv=android-managed`.

## iOS simulator (macOS)

1. Install Xcode and its command-line tools, open Xcode once and install an iOS simulator runtime.
2. `appium driver install xcuitest`. The first session builds WebDriverAgent, which takes a few minutes.
3. Run:
   ```bash
   ./scripts/download-apps.sh ios
   ./mvnw verify -Denv=ios-local -Dsuite=suites/smoke.xml "-Dcaps.ios.deviceName=iPhone 16" -Dcaps.ios.platformVersion=18.5
   ```
   Use a simulator and version from `xcrun simctl list devices available`.

## Inspecting locators

[Appium Inspector](https://github.com/appium/appium-inspector) shows the element tree, accessibility ids,
resource ids and suggested locators of a running session. When a test fails, the *Page source on failure*
attachment in Allure contains the same tree.

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| `Could not start an Appium session on http://127.0.0.1:4723` | Appium not running, or the driver is not installed (`appium driver list --installed`) |
| `App not found: .../examples/apps/...` | Run `scripts/download-apps.*` or set `app.path` |
| `No free device within ...` | More test threads than devices in `devices` |
| Session starts but every element times out | Wrong app or activity on screen. Check the *Screenshot on failure* attachment. |
