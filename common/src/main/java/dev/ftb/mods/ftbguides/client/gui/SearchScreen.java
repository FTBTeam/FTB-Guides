package dev.ftb.mods.ftbguides.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.config.ClientConfig;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SearchScreen extends BaseScreen {
    private final GuideScreen guideScreen;
    private final TextBox textBox;
    private final SimpleButton searchButton;
    private final SimpleTextButton scopeButton;

    public SearchScreen(GuideScreen guideScreen) {
        this.guideScreen = guideScreen;

        textBox = new TextBox(this) {
            @Override
            public void onEnterPressed() {
                doSearch();
            }
        };
        textBox.setFocused(true);

        searchButton = new SimpleButton(this, Component.empty(), GuideScreen.SEARCH_ICON, (btn, mb) -> doSearch());
        scopeButton = new SimpleTextButton(this, Component.empty(), Icon.empty()) {
            @Override
            public void onClicked(MouseButton button) {
                ClientConfig.toggleSearchThisOnly();
                setScopeButtonText();
            }
        };
    }

    private void setScopeButtonText() {
        scopeButton.setTitle(Component.translatable("ftbguides.gui." + (ClientConfig.searchThisGuideOnly() ? "this_guide" : "all_guides")));
    }

    @Override
    public Theme getTheme() {
        return guideScreen.getTheme();
    }

    @Override
    public boolean onInit() {
        setSize(131, 68);

        setScopeButtonText();

        return true;
    }

    @Override
    public void drawBackground(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
        var matrixStack = guiGraphics.pose();
        matrixStack.translate(0, 0, -800);
        guideScreen.draw(guiGraphics, theme, x, y, w, h);
        matrixStack.translate(0, 0, 800);
        Color4I.DARK_GRAY.withAlpha(192).draw(guiGraphics, guideScreen.getX(), guideScreen.getY(), guideScreen.width, guideScreen.height);

        guideScreen.getGuideTheme().indexBgColor().draw(guiGraphics, x, y, w, h);
        GuiHelper.drawRectWithShade(guiGraphics, x, y, w, h, guideScreen.getGuideTheme().guiColor(), 16);
    }

    @Override
    public void drawForeground(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
        getTheme().drawString(guiGraphics, Component.translatable("gui.recipebook.search_hint"), posX + 5, posY + 9,
                guideScreen.getGuideTheme().textColor(), 0);
    }

    @Override
    public void addWidgets() {
        add(textBox);
        add(searchButton);
        add(scopeButton);
    }

    @Override
    public void alignWidgets() {
        textBox.setPosAndSize(5, 26, 100, 16);
        searchButton.setPosAndSize(110, 26, 16, 16);
        scopeButton.setPosAndSize((width - scopeButton.width) / 2, 47, scopeButton.width, 16);
    }

    private void doSearch() {
        guideScreen.run();
        if (!textBox.getText().isEmpty()) {
            guideScreen.showSearchResults(textBox.getText());
        }
    }
}
