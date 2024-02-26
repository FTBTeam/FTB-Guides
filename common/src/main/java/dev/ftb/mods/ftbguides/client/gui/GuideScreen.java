package dev.ftb.mods.ftbguides.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.client.gui.widgets.Anchorable;
import dev.ftb.mods.ftbguides.docs.DocRenderer;
import dev.ftb.mods.ftbguides.docs.DocsLoader;
import dev.ftb.mods.ftbguides.docs.DocsManager;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static dev.ftb.mods.ftbguides.FTBGuides.rl;

public class GuideScreen extends BaseScreen implements ClickEventHandler {
    public static final Icon PIN_ICON_IN = Icon.getIcon(rl("textures/gui/pin.png"));
    public static final Icon PIN_ICON_OUT = Icon.getIcon(rl("textures/gui/pin_out.png"));

    private final ToolbarPanel toolbarPanel;
    private final IndexPanel indexPanel;
    private final DocsPanel docsPanel;
    private final Set<String> collapsedCategories;
    private final PanelScrollBar docsScrollbar;
    private final ExpandIndexButton expandIndexButton;

    private double lastScrollPos;
    private final Deque<String> history = new ArrayDeque<>();

    // TODO persist these across client invocations?
    private static DocsLoader.NodeWithMeta activeNode = null;
    private static boolean indexPinned;

    public GuideScreen() {
        toolbarPanel = new ToolbarPanel();
        indexPanel = new IndexPanel();
        docsPanel = new DocsPanel();
        docsScrollbar = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, docsPanel) {
            @Override
            public boolean shouldDraw() {
                return getScrollBarSize() > 0;
            }
        };

        expandIndexButton = new ExpandIndexButton();
        collapsedCategories = new HashSet<>();
    }

    @Override
    public void addWidgets() {
        add(toolbarPanel);
        add(indexPanel);
        add(docsPanel);
        add(docsScrollbar);
        add(expandIndexButton);
    }

    @Override
    public void alignWidgets() {
        toolbarPanel.setPosAndSize(0, 0, width - 1, 20);
        docsPanel.setPosAndSize(20, 25, width - 20, height - 25);
        docsScrollbar.setPosAndSize(width - 12, docsPanel.getY(), 12, docsPanel.height);
        expandIndexButton.setPosAndSize(0, 20, 20, height - 20);

        toolbarPanel.alignWidgets();
    }

    @Override
    public boolean onInit() {
        return setFullscreen();
    }

    @Override
    public void tick() {
        super.tick();

        if (activeNode == null) {
            if (!navigateTo(FTBGuides.MOD_ID + ":index")) {
                FTBGuides.LOGGER.error("missing index page!");
                closeGui();
            }
        }
        lastScrollPos = docsPanel.getScrollY();
    }

    @Override
    public boolean keyPressed(Key key) {
        int lineHeight = getTheme().getFontHeight() + 2;
        switch (key.keyCode) {
            case InputConstants.KEY_DOWN ->
                    adjustScroll(lineHeight);
            case InputConstants.KEY_UP ->
                    adjustScroll( -lineHeight);
            case InputConstants.KEY_PAGEDOWN ->
                // scroll a bit less than a full page; provides a bit of continuity for the reader
                    adjustScroll(docsPanel.height - lineHeight * 2);
            case InputConstants.KEY_PAGEUP ->
                    adjustScroll(-(docsPanel.height - lineHeight * 2));
            case InputConstants.KEY_HOME ->
                    adjustScroll(-docsPanel.getContentHeight());
            case InputConstants.KEY_END ->
                    adjustScroll(docsPanel.getContentHeight());
        }
        return super.keyPressed(key);
    }

    @Override
    public void onBack() {
        if (history.size() >= 2) {
            history.pop();
            navigateTo(Objects.requireNonNull(history.peekFirst()), false);
        }
    }

    private void adjustScroll(int amount) {
        docsScrollbar.setValue(docsScrollbar.getValue() + amount);
    }

    private void setActivePage(DocsLoader.NodeWithMeta node) {
        activeNode = node;
        lastScrollPos = 0d;
        docsPanel.refreshWidgets();
        docsScrollbar.setValue(0);
        docsScrollbar.onMoved();
    }

    private void toggleExpanded(String catName) {
        if (collapsedCategories.contains(catName)) {
            collapsedCategories.remove(catName);
        } else {
            collapsedCategories.add(catName);
        }
        indexPanel.refreshWidgets();
    }

    @Override
    public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
        Color4I.BLACK.withAlpha(128).draw(matrixStack, x, y, w, h);
    }

    @Override
    public Theme getTheme() {
        return GuideTheme.THEME;
    }

    @Override
    public boolean handleClickEvent(ClickEvent event) {
        try {
            URI uri = new URI(event.getValue());
            if (uri.getScheme() == null) {
                return navigateTo(event.getValue());
            }
        } catch (URISyntaxException ignored) {
        }
        return false;
    }

    public boolean navigateTo(String target) {
        return navigateTo(target, true);
    }

    public boolean navigateTo(String target, boolean addToHistory) {
        String[] parts = target.split("#");
        String pageId = parts[0];
        String anchor = parts.length >= 2 ? parts[1] : "";

        pageId = pageId.replaceAll("\\.md$", "");

        if (pageId.isEmpty() && !anchor.isEmpty()) {
            // just jumping to an anchor in the current doc
            if (scrollToAnchor(anchor)) {
                if (addToHistory) addToHistory(anchor);
            }
        } else {
            if (ResourceLocation.isValidResourceLocation(pageId)) {
                ResourceLocation newPage = pageId.contains(":") ?
                        new ResourceLocation(pageId) :
                        activeNode != null ? new ResourceLocation(activeNode.pageId().getNamespace(), pageId) : rl(pageId);
                if (setActivePage(newPage) && (anchor.isEmpty() || scrollToAnchor(anchor))) {
                    if (addToHistory) addToHistory(anchor);
                } else {
                    FTBGuides.LOGGER.warn("can't navigate to {}", target);
                }
            }
        }

        return true;
    }

    private void addToHistory(String anchor) {
        if (activeNode != null) {
            String entry = activeNode.pageId().toString();
            if (!anchor.isEmpty()) {
                entry = entry + "#" + anchor;
            }
            if (!entry.equals(history.peekFirst())) {
                history.push(entry);
                if (history.size() > 100) {
                    history.removeLast();
                }
            }
        }
    }

    public boolean setActivePage(ResourceLocation id) {
        return DocsManager.INSTANCE.get(id).map(node -> {
            setActivePage(node);
            return true;
        }).orElse(false);
    }

    public boolean scrollToAnchor(String anchorName) {
        if (anchorName.isEmpty()) {
            return false;
        }
        for (Widget w : docsPanel.widgets) {
            if (w instanceof Anchorable a && a.getAnchorName().equals(anchorName)) {
                docsScrollbar.setValue(w.posY);
                return true;
            }
        }

        FTBGuides.LOGGER.warn("unknown anchor #{} in doc {}", anchorName, activeNode.pageId());
        return false;
    }

    private class IndexPanel extends Panel {
        private boolean expanded = indexPinned;

        public IndexPanel() {
            super(GuideScreen.this);
        }

        @Override
        public void addWidgets() {
            DocsManager.INSTANCE.visit((catName, node) -> {
                if (node == null) {
                    // starting a new category
                    add(new SubcategoryButton(catName, Color4I.EMPTY));
                } else if (!collapsedCategories.contains(catName)) {
                    add(new DocsNodeButton(node));
                }
            });
        }

        @Override
        public void alignWidgets() {
            int maxW = 100;

            int hardMax = getScreen().getGuiScaledWidth() / 4;
            for (Widget w : widgets) {
                maxW = Math.min(Math.max(maxW, w.width), hardMax);
            }

            setPosAndSize(expanded || indexPinned ? 0 : -maxW, 20, maxW, getGui().height - 21);

            for (Widget w : widgets) {
                w.setX(2);
                w.setWidth(maxW - 4);
            }

            align(WidgetLayout.VERTICAL);

            if (getContentHeight() <= height) {
                setScrollY(0);
            }
        }

        @Override
        public void updateMouseOver(int mouseX, int mouseY) {
            super.updateMouseOver(mouseX, mouseY);

            if (expanded && !indexPinned && !isMouseOver()) {
                setExpanded(false);
            }
        }

        private void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        @Override
        public int getX() {
            return expanded || indexPinned ? 0 : -width;
        }

        @Override
        public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
            theme.drawContextMenuBackground(matrixStack, x, y, w, h);
        }

        @Override
        public void draw(PoseStack poseStack, Theme theme, int x, int y, int w, int h) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 600);
            RenderSystem.enableDepthTest();
            super.draw(poseStack, theme, x, y, w, h);
            poseStack.popPose();
        }

        private abstract class ListButton extends SimpleTextButton {
            public ListButton(Component title, Icon icon) {
                super(IndexPanel.this, title, icon);
                setHeight(16);
            }

            @Override
            public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
            }
        }

        public class DocsNodeButton extends ListButton {
            private final DocsLoader.NodeWithMeta node;

            public DocsNodeButton(DocsLoader.NodeWithMeta node) {
                super(Component.literal(node.metadata().title()), node.metadata().makeIcon());
                this.node = node;
            }

            @Override
            public void onClicked(MouseButton button) {
                setActivePage(node);
                addToHistory("");
            }

            @Override
            public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
                if (node == activeNode) {
                    NordColors.POLAR_NIGHT_2.draw(matrixStack, x, y, w, h);
                }
                super.draw(matrixStack, theme, x, y, w, h);
            }
        }

        public class SubcategoryButton extends ListButton {
            private final String catName;

            public SubcategoryButton(String catName, Icon icon) {
                super(makeTitle(collapsedCategories, catName), icon);
                this.catName = catName;
            }

            @Override
            public void onClicked(MouseButton button) {
                toggleExpanded(catName);
                indexPanel.refreshWidgets();
            }

            private static Component makeTitle(Set<String> e, String catName) {
                return Component.literal(e.contains(catName) ? "▼ " : "▶ ").append(catName);
            }

            @Override
            public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
                super.draw(matrixStack, theme, x, y, w, h);
            }
        }
    }

    private class DocsPanel extends Panel {
        public DocsPanel() {
            super(GuideScreen.this);
        }

        @Override
        public void addWidgets() {
            if (activeNode != null) {
                addAll(DocRenderer.create().parse(activeNode.node(), this));
            }
        }

        @Override
        public void alignWidgets() {
            align(new WidgetLayout.Vertical(0, 4, 0));

            docsScrollbar.setMaxValue(getContentHeight());
            docsScrollbar.setValue(lastScrollPos);
        }

        @Override
        public void tick() {
            super.tick();

            int prevX = getX();
            int prevW = width;

            int sbWidth = docsScrollbar.shouldDraw() ? docsScrollbar.width : 0;
            setX(indexPanel.expanded || indexPinned ? indexPanel.getX() + indexPanel.width + 5 : 25);
            setWidth(getScreen().getGuiScaledWidth() - docsPanel.getX() - 5 - sbWidth);

            if (prevX != getX() || prevW != width) {
                refreshWidgets();
            }
        }
    }

    private class ToolbarPanel extends Panel {
        private final Button pinButton;
        private final SimpleButton closeButton;

        public ToolbarPanel() {
            super(GuideScreen.this);

            pinButton = new SimpleButton(this, Component.empty(), indexPinned ? PIN_ICON_IN : PIN_ICON_OUT, (btn, mb) -> {
                indexPinned = !indexPinned;
                btn.setIcon(indexPinned ? PIN_ICON_IN : PIN_ICON_OUT);
            });
            closeButton = new SimpleButton(this, Component.empty(), Icons.CLOSE, (b, mb) -> closeGui());
        }

        @Override
        public void addWidgets() {
            add(pinButton);
            add(closeButton);
        }

        @Override
        public void alignWidgets() {
            pinButton.setPosAndSize(2, 2, 16, 16);
            closeButton.setPosAndSize(width - 16, 3, 15, 15);
        }

        @Override
        public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
            Color4I.rgb(60, 60, 60).draw(matrixStack, x, y + h - 1, w, 1);
        }
    }

    private class ExpandIndexButton extends Widget {
        public ExpandIndexButton() {
            super(GuideScreen.this);
        }

        @Override
        public void draw(PoseStack poseStack, Theme theme, int x, int y, int w, int h) {
            if (!indexPanel.expanded) {
                GuiHelper.drawHollowRect(poseStack, x, y, w, h, Color4I.rgb(0x202020), false);
                Icons.RIGHT.draw(poseStack, x + (w - 12) / 2, y + (h - 12) / 2, 12, 12);
            }
        }

        @Override
        public void updateMouseOver(int mouseX, int mouseY) {
            super.updateMouseOver(mouseX, mouseY);

            if (!indexPanel.expanded && isMouseOver()) {
                indexPanel.setExpanded(true);
            }
        }
    }
}
