package io.github.alexxfromgit.taf.mobile.examples.mydemo.lint;

import io.github.alexxfromgit.taf.mobile.core.lint.MetadataLinter;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

import static io.github.alexxfromgit.taf.mobile.examples.mydemo.support.Owners.PLATFORM_TEAM;

/**
 * Runs first in every suite and needs no device: undocumented tests, screens without identifiers or leaked
 * secrets fail the build before any device time is spent.
 */
@Epic("Quality gates")
@Feature("Test metadata")
@Owner(PLATFORM_TEAM)
public class MetadataLintTest {

    @Test(description = "Tests have owner and feature, screens have identifiers, no secrets in properties")
    public void testsAndScreensAreWellFormed() {
        MetadataLinter.forPackages("io.github.alexxfromgit.taf.mobile.examples").assertClean();
    }
}
