package io.github.alexxfromgit.taf.mobile.examples.mydemo.screens;

import io.github.alexxfromgit.taf.mobile.core.screen.ScreenMixin;

/** Mixin for every screen that shows the app navigation. */
public interface HasHeader extends ScreenMixin {

    default Header header() {
        return component(Header.class);
    }
}
