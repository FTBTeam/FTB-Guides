package dev.ftb.mods.ftbguides.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.docs.DocParser;
import dev.ftb.mods.ftbguides.docs.DocsLoader;
import dev.ftb.mods.ftbguides.docs.DocsManager;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;

import javax.print.Doc;
import java.util.HashSet;
import java.util.Set;

public class GuideScreen extends BaseScreen {
    private final IndexPanel indexPanel;
    private final DocsPanel docsPanel;
    private final ExpandIndexButton expandIndexButton;
    private final Set<String> expandedCategories;
    private DocsLoader.NodeWithMeta activeNode = null;

    public GuideScreen() {
        indexPanel = new IndexPanel();
        docsPanel = new DocsPanel();
        expandIndexButton = new ExpandIndexButton();

        expandedCategories = new HashSet<>();
    }

    @Override
    public void addWidgets() {
        add(indexPanel);
        add(docsPanel);
        add(expandIndexButton);
    }

    @Override
    public void alignWidgets() {
        docsPanel.setPosAndSize(0, 0, width, height);
        expandIndexButton.setPosAndSize(0, 0, 20, height);
    }

    @Override
    public boolean onInit() {
        return setFullscreen();
    }

    private void setActivePage(DocsLoader.NodeWithMeta node) {
        activeNode = node;
        docsPanel.refreshWidgets();
    }

    private void toggleExpanded(String catName) {
        if (expandedCategories.contains(catName)) {
            expandedCategories.remove(catName);
        } else {
            expandedCategories.add(catName);
        }
        indexPanel.refreshWidgets();
    }

    @Override
    public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
//        Color4I.BLACK.withAlpha(192).draw(matrixStack, x, y, w, h);
    }

    private static Icon getIcon(DocsLoader.NodeWithMeta node) {
        return node.metadata().icon().map(Icon::getIcon).orElse(Color4I.EMPTY);
    }

    private class IndexPanel extends Panel {
        private static boolean pinned; // TODO persist across client invocations?
        private boolean expanded = pinned;

        public IndexPanel() {
            super(GuideScreen.this);
        }

        @Override
        public void addWidgets() {
            DocsManager.INSTANCE.visit((name, node) -> {
                if (node == null) {
                    // starting a new category
                    add(new SubcategoryButton(name, Color4I.EMPTY));
                } else if (expandedCategories.contains(node.metadata().category())) {
                    add(new DocsNodeButton(node));
                }
            });
        }

        @Override
        public void alignWidgets() {
            int maxW = 100;

            for (Widget w : widgets) {
                maxW = Math.min(Math.max(maxW, ((ListButton) w).getActualWidth(getGui())), 800);
            }

            setPosAndSize(expanded || pinned ? 0 : -maxW, 0, maxW, getGui().height);

            for (Widget w : widgets) {
                w.setWidth(maxW);
            }

            align(WidgetLayout.VERTICAL);

            if (getContentHeight() <= height) {
                setScrollY(0);
            }
        }

        @Override
        public void updateMouseOver(int mouseX, int mouseY) {
            super.updateMouseOver(mouseX, mouseY);

            if (expanded && !pinned && !isMouseOver()) {
                setExpanded(false);
            }
        }

        private void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        @Override
        public int getX() {
            return expanded || pinned ? 0 : -width;
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
            }

            public int getActualWidth(BaseScreen screen) {
                return screen.getTheme().getStringWidth(title) + 20;
            }
        }

        public class DocsNodeButton extends ListButton {
            private final DocsLoader.NodeWithMeta node;

            public DocsNodeButton(DocsLoader.NodeWithMeta node) {
                super(Component.literal(node.metadata().title()), getIcon(node));
                this.node = node;
            }

            @Override
            public void onClicked(MouseButton button) {
                setActivePage(node);
            }

            @Override
            public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
                super.draw(matrixStack, theme, x, y, w, h);
            }
        }

        public class SubcategoryButton extends ListButton {
            private final String catName;

            public SubcategoryButton(String catName, Icon icon) {
                super(makeTitle(expandedCategories, catName), icon);
                this.catName = catName;
            }

            @Override
            public void onClicked(MouseButton button) {
                toggleExpanded(catName);
                indexPanel.refreshWidgets();
            }

            private static Component makeTitle(Set<String> e, String catName) {
                return Component.literal(e.contains(catName) ? "v " : "> ").append(catName);
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
            // TODO call Node rendering here to get a list of widgets
            if (activeNode != null) {
                addAll(DocParser.create().parse(activeNode.node(), this));
            }
        }

        @Override
        public void alignWidgets() {
            align(WidgetLayout.VERTICAL);
        }
    }

    private class ExpandIndexButton extends Widget {
        public ExpandIndexButton() {
            super(GuideScreen.this);
        }

        @Override
        public void draw(PoseStack poseStack, Theme theme, int x, int y, int w, int h) {
            if (!indexPanel.expanded) {
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
