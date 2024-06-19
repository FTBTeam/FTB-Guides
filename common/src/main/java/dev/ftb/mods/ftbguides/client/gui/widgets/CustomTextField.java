package dev.ftb.mods.ftbguides.client.gui.widgets;

import dev.ftb.mods.ftbguides.client.FTBGuidesClient;
import dev.ftb.mods.ftbguides.client.gui.ClickEventHandler;
import dev.ftb.mods.ftbguides.client.gui.GuideThemeProvider;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class CustomTextField extends TextField implements Anchorable {
    private String anchorName = "";

    public CustomTextField(Panel panel, Component text) {
        super(panel);

        setMaxWidth(panel.width);
        setText(text.copy());
        setColor(GuideThemeProvider.getGuideThemeFor(this).textColor());
    }

    @Override
    public boolean mousePressed(MouseButton button) {
        if (isMouseOver()) {
            if (button.isLeft() && Minecraft.getInstance().screen != null) {
                Style style = getComponentStyleAt(getGui().getTheme(), getMouseX(), getMouseY());
                if (style != null) {
                    return handleCustomClickEvent(style) || Minecraft.getInstance().screen.handleComponentClicked(style);
                }
            }
        }

        return super.mousePressed(button);
    }

    @Override
    public void addMouseOverText(TooltipList list) {
        Style style = getComponentStyleAt(getGui().getTheme(), getMouseX(), getMouseY());
        if (style != null && style.getClickEvent() != null) {
            list.add(Component.literal(style.getClickEvent().getValue()).withStyle(ChatFormatting.GRAY));
        }
    }

    private boolean handleCustomClickEvent(Style style) {
        if (style == null) return false;

        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) return false;

        if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
            return getGui() instanceof ClickEventHandler h && h.handleClickEvent(clickEvent);
        }

        return false;
    }

    @Override
    public String getAnchorName() {
        return anchorName;
    }

    @Override
    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }
}
