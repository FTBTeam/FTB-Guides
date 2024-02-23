package dev.ftb.mods.ftbguides.client.gui.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetLayout;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BlockQuotePanel extends Panel {
    private List<Widget> widgets = new ArrayList<>();

    public BlockQuotePanel(Panel panel) {
        super(panel);
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    @Override
    public void addWidgets() {
        addAll(widgets);
    }

    @Override
    public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        // Draw a line to the left of the panel
        Color4I.GRAY.draw(matrixStack, x, y, 2, h);
    }

    @Override
    public void alignWidgets() {
        align(new WidgetLayout.Vertical(0, 4, 0));
        widgets.forEach(w -> w.setX(4));
        setWidth(widgets.stream().map(w -> w.width).max(Comparator.naturalOrder()).orElse(12) + 4);
        setHeight(widgets.stream().map(w -> w.height).reduce(0, Integer::sum) + 4 * (widgets.size() - 1));
    }
}
