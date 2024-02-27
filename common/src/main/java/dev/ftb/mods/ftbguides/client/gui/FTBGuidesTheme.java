package dev.ftb.mods.ftbguides.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.docs.GuideIndex;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.WidgetType;

public class FTBGuidesTheme extends Theme {
    private final GuideScreen screen;

    public FTBGuidesTheme(GuideScreen screen) {
        this.screen = screen;
    }

    private GuideIndex.GuideTheme getGuideTheme() {
        return screen.getGuideTheme();
    }

    @Override
    public void drawContextMenuBackground(PoseStack matrixStack, int x, int y, int w, int h) {
        getGuideTheme().indexBgColor().draw(matrixStack, x, y, w, h);
        GuiHelper.drawRectWithShade(matrixStack, x, y, w, h, getGuideTheme().guiColor(), 16);
    }

    @Override
    public void drawScrollBarBackground(PoseStack matrixStack, int x, int y, int w, int h, WidgetType type) {
        getGuideTheme().indexBgColor().draw(matrixStack, x, y, w, h);
        GuiHelper.drawRectWithShade(matrixStack, x, y, w, h, getGuideTheme().guiColor(), -16);
    }

    @Override
    public void drawScrollBar(PoseStack matrixStack, int x, int y, int w, int h, WidgetType type, boolean vertical) {
        getGuideTheme().guiColor().withAlpha(128).draw(matrixStack, x + 2, y + 1, w - 4, h - 2);
        GuiHelper.drawRectWithShade(matrixStack, x + 2, y + 1, w - 4, h - 2, getGuideTheme().guiColor(), 16);
    }
}
