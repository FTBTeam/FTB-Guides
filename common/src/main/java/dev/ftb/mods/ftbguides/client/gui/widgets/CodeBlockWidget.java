package dev.ftb.mods.ftbguides.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CodeBlockWidget extends Widget {
    List<Component> lines;

    public CodeBlockWidget(Panel p, List<Component> lines) {
        super(p);
        this.lines = lines;
        this.setHeight(lines.size() * 10);
    }

    @Override
    public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        Screen.fill(matrixStack, x, y + 20, x + w, y + 21, 0xFF000000);
        for (int i = 0; i < lines.size(); i++) {
            theme.drawString(matrixStack, lines.get(i), x, y + i * 10, 0xFFFFFFFF);
        }
    }
}
