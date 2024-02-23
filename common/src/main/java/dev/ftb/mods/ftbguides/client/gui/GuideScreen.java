package dev.ftb.mods.ftbguides.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.client.gui.widgets.Anchorable;
import dev.ftb.mods.ftbguides.client.gui.widgets.CustomTextField;
import dev.ftb.mods.ftbguides.docs.DocRenderer;
import dev.ftb.mods.ftbguides.docs.DocsLoader;
import dev.ftb.mods.ftbguides.docs.DocsManager;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static dev.ftb.mods.ftbguides.FTBGuides.rl;

public class GuideScreen extends BaseScreen implements ClickEventHandler {
    public static final Icon PIN_ICON_IN = Icon.getIcon(rl("textures/gui/pin.png"));
    public static final Icon PIN_ICON_OUT = Icon.getIcon(rl("textures/gui/pin_out.png"));

    private final ToolbarPanel toolbarPanel;
    private final IndexPanel indexPanel;
    private final DocsPanel docsPanel;
    private final Set<String> collapsedCategories;
    private final ExpandIndexButton expandIndexButton;

    private double lastScrollPos;

    // TODO persist these across client invocations?
    private static DocsLoader.NodeWithMeta activeNode = null;
    private static boolean indexPinned;

    public GuideScreen() {
        toolbarPanel = new ToolbarPanel();
        indexPanel = new IndexPanel();
        docsPanel = new DocsPanel();

        expandIndexButton = new ExpandIndexButton();
        collapsedCategories = new HashSet<>();
    }

    @Override
    public void addWidgets() {
        add(toolbarPanel);
        add(indexPanel);
        add(docsPanel);
        add(expandIndexButton);
    }

    @Override
    public void alignWidgets() {
        toolbarPanel.setPosAndSize(0, 0, width - 1, 20);
        docsPanel.setPosAndSize(20, 25, width - 20, height - 25);
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
            DocsManager.INSTANCE.get(rl("index")).ifPresent(this::setActivePage);
        }
        lastScrollPos = docsPanel.getScrollY();
    }

    private void setActivePage(DocsLoader.NodeWithMeta node) {
        activeNode = node;
        lastScrollPos = 0d;
        docsPanel.refreshWidgets();
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
        if (activeNode != null && event.getValue().startsWith("#")) {
            String anchorName = event.getValue().substring(1).toLowerCase(Locale.ROOT);
            for (Widget w : docsPanel.widgets) {
                if (w instanceof Anchorable a && a.getAnchorName().equals(anchorName)) {
                    docsPanel.setScrollY(w.posY);
                    return true;
                }
            }
        }
        return false;
    }

    public void setActivePage(ResourceLocation id) {
        DocsManager.INSTANCE.get(id).ifPresent(this::setActivePage);
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

            if (lastScrollPos < docsPanel.getContentHeight()) {
                setScrollY(lastScrollPos);
            } else {
                lastScrollPos = 0;
                setScrollY(0);
            }
        }

        @Override
        public void tick() {
            super.tick();

            int x0 = getX();
            int w = width;

            setX(indexPanel.expanded || indexPinned ? indexPanel.getX() + indexPanel.width + 5 : 25);
            setWidth(getScreen().getGuiScaledWidth() - docsPanel.getX() - 5);

            if (x0 != getX() || w != width) {
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
            closeButton.setPosAndSize(width - 18, 3, 15, 15);
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
