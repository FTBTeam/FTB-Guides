package dev.ftb.mods.ftbguides.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.List;

public class CodeBlockWidget extends Widget {
    List<Component> lines;

    public CodeBlockWidget(Panel p, List<Component> lines) {
        super(p);
        this.lines = lines;

        Theme theme = p.getGui().getTheme();
        setHeight(lines.size() * (theme.getFontHeight() + 1) + 6);
        setWidth(lines.stream().map(theme::getStringWidth).max(Comparator.naturalOrder()).orElse(10) + 6);
    }

    @Override
    public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        Color4I.DARK_GRAY.draw(matrixStack, x, y, w, h);
        GuiHelper.drawHollowRect(matrixStack, x, y, w, h, Color4I.GRAY.withAlpha(80), false);
        for (int i = 0; i < lines.size(); i++) {
            theme.drawString(matrixStack, lines.get(i), x + 2, y + 3 + i * 10, Color4I.rgb(0xEBCB8B), 0);
        }
    }
}
