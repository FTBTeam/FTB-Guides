package dev.ftb.mods.ftbguides.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.network.chat.Component;

public class IconButton extends SimpleButton {
    public IconButton(Panel panel, Component text, Icon icon, Callback c) {
        super(panel, text, icon, c);
    }

    @Override
    public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        GuiHelper.setupDrawing();
        drawBackground(matrixStack, theme, x, y, w, h);
        drawIcon(matrixStack, theme, x, y, w, h);
    }
}
