package dev.ftb.mods.ftbguides.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.client.gui.GuideThemeProvider;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.gui.GuiGraphics;

public class LineBreakWidget extends Widget {
    public LineBreakWidget(Panel p) {
        super(p);
        setWidth(p.width);
        setHeight(12);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
        GuideThemeProvider.getGuideThemeFor(this).guiColor().draw(guiGraphics, x + 20, y + (height - 2) / 2, w - 40, 1);
    }
}
