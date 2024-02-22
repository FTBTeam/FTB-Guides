package dev.ftb.mods.ftbguides.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CodeBlockWidget extends Widget {
    List<Component> lines;

    public CodeBlockWidget(Panel p, List<Component> lines) {
        super(p);
        this.lines = lines;
    }
}
