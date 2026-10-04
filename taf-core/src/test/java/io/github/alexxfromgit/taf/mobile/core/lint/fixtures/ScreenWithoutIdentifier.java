package io.github.alexxfromgit.taf.mobile.core.lint.fixtures;

import io.github.alexxfromgit.taf.mobile.core.screen.Locate;
import io.github.alexxfromgit.taf.mobile.core.screen.Screen;
import io.github.alexxfromgit.taf.mobile.core.screen.UiElement;

/** Linter fixture: a screen that cannot tell when it is open. */
public class ScreenWithoutIdentifier extends Screen {

    @Locate("button")
    private UiElement button;
}
