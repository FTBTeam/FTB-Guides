package dev.ftb.mods.ftbguides.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.network.chat.Component;

public class BlockQuoteWidget extends Widget {
    Component component;

    public BlockQuoteWidget(Panel p, Component component) {
        super(p);
        this.component = component;
    }
}
