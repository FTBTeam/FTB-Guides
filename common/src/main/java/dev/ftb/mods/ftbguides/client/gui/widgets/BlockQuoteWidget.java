package dev.ftb.mods.ftbguides.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BlockQuoteWidget extends Widget {
    Component component;

    public BlockQuoteWidget(Panel p, Component component) {
        super(p);
        this.component = component;
        this.setHeight(20);
    }

    @Override
    public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        theme.drawString(matrixStack, component, x, y, 0xFF000000);
        Screen.fill(matrixStack, x, y + 20, x + w, y + 21, 0xFF000000);
    }
}
