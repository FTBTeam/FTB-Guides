package dev.ftb.mods.ftbguides.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;

public class GuideTheme extends Theme {
    public static GuideTheme THEME = new GuideTheme();

    @Override
    public void drawContextMenuBackground(PoseStack matrixStack, int x, int y, int w, int h) {
        Color4I.DARK_GRAY.draw(matrixStack, x, y, w, h);
        GuiHelper.drawRectWithShade(matrixStack, x, y, w, h, Color4I.rgb(0x606060), 16);
    }
}
