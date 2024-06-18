package dev.ftb.mods.ftbguides.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class IconButton extends SimpleButton {
    public IconButton(Panel panel, Component text, Icon icon, Callback c) {
        super(panel, text, icon, c);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
        GuiHelper.setupDrawing();
        drawBackground(guiGraphics, theme, x, y, w, h);
        drawIcon(guiGraphics, theme, x, y, w, h);
    }

    @Override
    public void onClicked(MouseButton button) {
        // nothing; suppress the click sound
    }

    @Override
    public CursorType getCursor() {
        return null;
    }
}
