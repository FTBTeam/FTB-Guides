package dev.ftb.mods.ftbguides.client.gui.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetLayout;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
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
        Screen.fill(matrixStack, x, y, x + 2, y + h, 0xFFFFFFFF);
    }

    @Override
    public void alignWidgets() {
        align(WidgetLayout.VERTICAL);
    }
}
