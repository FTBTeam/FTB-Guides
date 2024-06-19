package dev.ftb.mods.ftbguides.client.gui;

import dev.ftb.mods.ftbguides.docs.GuideIndex;
import dev.ftb.mods.ftblibrary.ui.Widget;

public interface GuideThemeProvider {
    GuideIndex.GuideTheme getGuideTheme();

    static GuideIndex.GuideTheme getGuideThemeFor(Widget w) {
        return w.getGui() instanceof GuideThemeProvider p ? p.getGuideTheme() : GuideIndex.GuideTheme.FALLBACK;
    }
}
