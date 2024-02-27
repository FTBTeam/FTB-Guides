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
            if (getGui() instanceof ClickEventHandler h && h.handleClickEvent(clickEvent)) {
                return true;
            }
//            try {
//
//                URI uri = new URI(clickEvent.getValue());
//                String scheme = uri.getScheme();
//                if (scheme == null) {
//                    throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
//                }
////                if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
////                    throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + scheme.toLowerCase(Locale.ROOT));
////                }
//            } catch (URISyntaxException e) {
//                errorToPlayer("Can't open url for %s (%s)", clickEvent.getValue(), e.getMessage());
//            }
        }
        return false;
    }

    private void errorToPlayer(String msg, Object... args) {
        FTBGuidesClient.displayError(Component.literal(String.format(msg, args)).withStyle(ChatFormatting.RED));
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
