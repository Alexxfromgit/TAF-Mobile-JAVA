package io.github.alexxfromgit.taf.mobile.core.lint.fixtures;

import org.testng.annotations.Test;

/** Linter fixture: a test without @Owner and @Feature. Not run by Surefire (name does not end with Test). */
public class UndocumentedFixture {

    @Test
    public void noMetadata() {
    }
}
