package io.github.alexxfromgit.taf.mobile.core.lint;

import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MetadataLinterTest {

    @Test
    public void reportsMissingMetadataAndScreenIdentifiers() {
        List<LintViolation> violations = MetadataLinter
                .forPackages("io.github.alexxfromgit.taf.mobile.core.lint.fixtures").check();

        assertThat(violations).extracting(LintViolation::toString).containsExactlyInAnyOrder(
                "UndocumentedFixture.noMetadata: missing @Owner (who maintains this test?)",
                "UndocumentedFixture.noMetadata: missing @Epic/@Feature/@Story (where does it belong in the report?)",
                "ScreenWithoutIdentifier: screen has no @ScreenIdentifier field - waitReady() cannot tell when it is open");
    }
}
