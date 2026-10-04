package io.github.alexxfromgit.taf.mobile.core.lint.fixtures;

import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.ScreenIdentifier;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;

/** Linter fixture: everything in order. */
@Owner("someone")
@Feature("Something")
public class DocumentedFixture {

    @Test
    public void documented() {
    }

    public static class GoodScreen extends Screen {
        @ScreenIdentifier
        @Locate("title")
        private UiElement title;
    }
}
