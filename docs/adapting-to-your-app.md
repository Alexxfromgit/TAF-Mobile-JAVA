# Adapting the template to your app

## 1. Create your repository

Click **Use this template** (or clone and re-initialise git) and check the device-free build:

```bash
./mvnw verify
```

## 2. Rename (optional but recommended)

- `pom.xml` files: `groupId` (`io.github.alexxfromgit`), name and URL.
- Packages `io.github.alexxfromgit.taf.mobile.*`: use your IDE's *Refactor > Rename package*.
- `taf-core/src/main/resources/META-INF/services/org.testng.ITestNGListener`: update the class names.
- `taf-core/src/main/resources/taf/defaults.properties`: update the `retry.on` entry for `EnvironmentException`.
- Suites in `examples/src/test/resources/suites/`: package and class names.

## 3. Point it at your app

`examples/src/test/resources/taf.properties`:

```properties
app.id.android=com.example.shop
app.id.ios=com.example.shop
users.standard.username=qa.standard@example.com    # password: USERS_STANDARD_PASSWORD env var
caps.android.appWaitActivity=com.example.shop.*
```

`examples/src/test/resources/env/android-local.properties`:

```properties
app.path=../app/build/outputs/apk/debug/app-debug.apk
```

## 4. Model your screens

Start with the screens of your smoke flow. For each one:
1. Pick one or two elements that prove the screen is open, and mark them `@ScreenIdentifier`.
2. Add the elements the tests use, preferably by accessibility id.
3. Turn repeated parts (header, list rows, dialogs) into `Component`s, and shared ones into mixins.
4. Make navigation methods return the next screen (`tapAndExpect(Next.class)`).

See [screens-and-locators.md](screens-and-locators.md). Ask your developers to add accessibility ids to key
elements. It's the single best investment in UI test stability, and it helps screen-reader users too.

## 5. Write tests

```java
@Epic("Shop") @Feature("Search") @Owner("search-team")
public class SearchTest extends MobileTestBase {

    @Test(groups = Groups.SMOKE)
    @TestUser("standard")
    @LoggedIn
    public void searchFindsProducts() {
        List<String> titles = on(HomeScreen.class).search("shoes").resultTitles();
        Verify.that(!titles.isEmpty(), "Search returns results");
    }
}
```

Move setup into preconditions ([sessions.md](sessions.md)) so test bodies only contain the behaviour under test.

## 6. Remove the examples

Delete `examples/.../mydemo`, its env files and the demo users. Keep `lint/MetadataLintTest` and the `MobileReports`
block in the suites.

## 7. CI

- `ci.yml` runs on every push without devices.
- `android-e2e.yml` builds an emulator on a Linux runner. Replace `scripts/download-apps.sh android` with your app
  build (for example `./gradlew assembleDebug` in your app repository, or downloading the artifact).
- `ios-e2e.yml` (manual) runs on a macOS runner.
- For device clouds, add `CLOUD_USERNAME` / `CLOUD_ACCESS_KEY` as repository secrets and run with
  `-Denv=browserstack-android` (or your own env file).
- Enable *Settings > Pages > Source: GitHub Actions* to publish the Allure report.
