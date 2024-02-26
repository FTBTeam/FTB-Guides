package dev.ftb.mods.ftbguides.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;

public class GuideTheme extends Theme {
    public static GuideTheme THEME = new GuideTheme();

    @Override
    public void drawContextMenuBackground(PoseStack matrixStack, int x, int y, int w, int h) {
        Color4I.DARK_GRAY.draw(matrixStack, x, y, w, h);
        GuiHelper.drawRectWithShade(matrixStack, x, y, w, h, Color4I.rgb(0x606060), 16);
    }

    @Override
    public void drawScrollBarBackground(PoseStack matrixStack, int x, int y, int w, int h, WidgetType type) {
        Color4I.DARK_GRAY.draw(matrixStack, x, y, w, h);
        GuiHelper.drawRectWithShade(matrixStack, x, y, w, h, Color4I.rgb(0x404040), -16);
    }

    @Override
    public void drawScrollBar(PoseStack matrixStack, int x, int y, int w, int h, WidgetType type, boolean vertical) {
        Color4I.rgb(0x606060).withAlpha(128).draw(matrixStack, x + 2, y + 1, w - 4, h - 2);
        GuiHelper.drawRectWithShade(matrixStack, x + 2, y + 1, w - 4, h - 2, Color4I.rgb(0x606060), 16);
    }
}
