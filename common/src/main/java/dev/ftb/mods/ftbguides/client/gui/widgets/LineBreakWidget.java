package dev.ftb.mods.ftbguides.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.client.gui.GuideThemeProvider;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;

public class LineBreakWidget extends Widget {
    public LineBreakWidget(Panel p) {
        super(p);
        setWidth(p.width);
        setHeight(12);
    }

    @Override
    public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        GuideThemeProvider.getGuideThemeFor(this).guiColor().draw(matrixStack, x + 20, y + (height - 2) / 2, w - 40, 1);
    }
}
