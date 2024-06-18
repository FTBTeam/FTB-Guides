package dev.ftb.mods.ftbguides.client.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.platform.InputConstants;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.client.FTBGuidesClient;
import dev.ftb.mods.ftbguides.client.gui.widgets.Anchorable;
import dev.ftb.mods.ftbguides.config.ClientConfig;
import dev.ftb.mods.ftbguides.docs.*;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.*;

import static dev.ftb.mods.ftbguides.FTBGuides.rl;

public class GuideScreen extends BaseScreen implements ClickEventHandler, GuideThemeProvider {
    public static final Icon PIN_ICON_IN = Icon.getIcon(rl("textures/gui/pin.png"));
    public static final Icon PIN_ICON_OUT = Icon.getIcon(rl("textures/gui/pin_out.png"));
    public static final Icon SEARCH_ICON = Icon.getIcon(rl("textures/gui/search.png"));
    public static final Icon BLANK_ICON = Color4I.BLACK.withAlpha(0);

    // these protocols get handled by vanilla when links are clicked
    private static final Set<String> VANILLA_PROTOCOLS = Set.of("http", "https", "file");

    private final ToolbarPanel toolbarPanel;
    private final IndexPanel indexPanel;
    private final DocsPanel docsPanel;
    private final Set<String> collapsedCategories;
    private final PanelScrollBar docsScrollbar;
    private final ExpandIndexButton expandIndexButton;
    private final Deque<String> history = new ArrayDeque<>();
    private final FTBGuidesTheme theme;

    private double lastScrollPos;
    private GuideIndex guideIndex;

    // TODO persist these across client invocations?
    private static DocsLoader.NodeWithMeta activeNode = null;

    public GuideScreen() {
        toolbarPanel = new ToolbarPanel();
        indexPanel = new IndexPanel();
        docsPanel = new DocsPanel();
        docsScrollbar = new PanelScrollBar(this, ScrollBar.Plane.VERTICAL, docsPanel);
        expandIndexButton = new ExpandIndexButton();
        collapsedCategories = new HashSet<>();
        theme = new FTBGuidesTheme(this);
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
            navigateTo(ClientConfig.HOME.get());
            if (activeNode == null) {
                FTBGuides.LOGGER.error("missing index page!");
                closeGui();
            }
        }
        if (guideIndex == null) {
            guideIndex = DocsManager.INSTANCE.getIndex(activeNode.pageId().getNamespace());
            indexPanel.refreshWidgets();
            if (guideIndex == null) {
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
            case InputConstants.KEY_HOME -> {
                if (ScreenWrapper.hasAltDown()) {
                    navigateTo(ClientConfig.HOME.get());
                } else {
                    adjustScroll(-docsPanel.getContentHeight());
                }
            }
            case InputConstants.KEY_END ->
                    adjustScroll(docsPanel.getContentHeight());
            case InputConstants.KEY_F -> {
                if (ScreenWrapper.hasControlDown()) {
                    new SearchScreen(this).openGuiLater();
                }
            }
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

    public void showSearchResults(String searchTerm) {
        final String searchL = searchTerm.toLowerCase(Locale.ROOT);

        String searchResults = Component.translatable("ftbguides.gui.search_results").getString();

        StringBuilder markdown = new StringBuilder();
        markdown.append("# ").append(searchResults).append("\n\n---\n\n");

        Multimap<String, DocsLoader.NodeWithMeta> matches = ArrayListMultimap.create();
        DocsManager mgr = DocsManager.INSTANCE;

        boolean thisGuideOnly = ClientConfig.searchThisGuideOnly();

        // find all matches by tag
        mgr.getNodesByTag(searchL).forEach(nodeWithMeta -> {
            if (!thisGuideOnly || nodeWithMeta.pageId().getNamespace().equals(activeNode.pageId().getNamespace())) {
                addSearchTarget(nodeWithMeta, mgr, matches);
            }
        });
        // find all matches by title
        mgr.allNodes().forEach(nodeWithMeta -> {
            if ((!thisGuideOnly || nodeWithMeta.pageId().getNamespace().equals(activeNode.pageId().getNamespace()))
                    && nodeWithMeta.metadata().title().toLowerCase(Locale.ROOT).contains(searchL)) {
                addSearchTarget(nodeWithMeta, mgr, matches);
            }
        });

        if (matches.isEmpty()) {
            markdown.append(Component.translatable("ftbguides.gui.no_matches", searchTerm).getString()).append("\n\n");
        } else {
            markdown.append(Component.translatable("ftbguides.gui.matches", matches.size(), searchTerm).getString()).append("\n");
            for (String title : matches.keySet().stream().sorted().toList()) {
                markdown.append("### ").append(title).append("\n");
                matches.get(title).forEach(nodeWithMeta ->
                        markdown.append(String.format("* [%s](%s)\n", nodeWithMeta.metadata().title(), nodeWithMeta.pageId()))
                );
                markdown.append("\n");
            }
        }
        markdown.append("---\n");
        if (history.peekFirst() != null) {
            markdown.append("[Go Back](").append(history.peekFirst()).append(")\n");
        }

        addToHistory("");

        Parser parser = Parser.builder().build();
        Node node = parser.parse(markdown.toString());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(activeNode.pageId().getNamespace(), "_search");

        setActivePage(new DocsLoader.NodeWithMeta(id, node, DocMetadata.searchResult(searchResults)));
    }

    private static void addSearchTarget(DocsLoader.NodeWithMeta nodeWithMeta, DocsManager mgr, Multimap<String, DocsLoader.NodeWithMeta> matches) {
        GuideIndex index = mgr.getIndex(nodeWithMeta.pageId().getNamespace());
        String guideName = index.categories().get(0).name();
        matches.put(guideName, nodeWithMeta);
    }

    private void adjustScroll(int amount) {
        docsScrollbar.setValue(docsScrollbar.getValue() + amount);
    }

    private void setActivePage(DocsLoader.NodeWithMeta node) {
        GuideIndex prevIndex = guideIndex;
        activeNode = node;
        guideIndex = DocsManager.INSTANCE.getIndex(node.pageId().getNamespace());
        lastScrollPos = 0d;

        if (guideIndex != prevIndex) {
            indexPanel.refreshWidgets();
        }
        docsPanel.refreshWidgets();
        docsScrollbar.setValue(0);
        docsScrollbar.onMoved();
    }

    private void toggleExpanded(String catId) {
        if (collapsedCategories.contains(catId)) {
            collapsedCategories.remove(catId);
        } else {
            collapsedCategories.add(catId);
        }
        indexPanel.refreshWidgets();
    }

    @Override
    public void drawBackground(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
        getGuideTheme().bgColor().draw(guiGraphics, x, y, w, h);
    }

    @Override
    public Theme getTheme() {
        return theme;
    }

    @Override
    public GuideIndex.GuideTheme getGuideTheme() {
        return guideIndex == null ? GuideIndex.GuideTheme.FALLBACK : guideIndex.theme();
    }

    @Override
    public boolean handleClickEvent(ClickEvent event) {
        String[] parts = event.getValue().split(":");
        if (parts.length > 1) {
            if (parts[0].equals("search")) {
                showSearchResults(parts[1]);
                return true;
            } else if (VANILLA_PROTOCOLS.contains(parts[0])) {
                return false; // let vanilla screen handle it
            }
        }
        return navigateTo(event.getValue());
    }

    public boolean navigateTo(String target) {
        return navigateTo(target, true);
    }

    public boolean navigateTo(String target, boolean addToHistory) {
        String[] parts = target.split("#");
        String pageId = parts[0];
        String anchor = parts.length >= 2 ? parts[1] : "";

        pageId = pageId.replaceAll("\\.md$", "");

        ResourceLocation location;
        if (pageId.isEmpty() && !anchor.isEmpty()) {
            // just jumping to an anchor in the current doc
            if (scrollToAnchor(anchor)) {
                if (addToHistory) addToHistory(anchor);
            }
        // TODO: I think I ported the parse logic wrong here
        } else if ((location = ResourceLocation.tryParse(pageId)) != null) {
            ResourceLocation newPage = pageId.contains(":") ?
                    location :
                    activeNode != null ? ResourceLocation.fromNamespaceAndPath(activeNode.pageId().getNamespace(), pageId) : rl(pageId);
            if (setActivePage(newPage) && (anchor.isEmpty() || scrollToAnchor(anchor))) {
                if (addToHistory) addToHistory(anchor);
            } else {
                FTBGuidesClient.displayError(Component.translatable("ftbguides.gui.cant_navigate", target));
                FTBGuides.LOGGER.warn("can't navigate to {}", target);
            }
        }

        return true;
    }

    private void addToHistory(String anchor) {
        if (activeNode != null && !activeNode.pageId().getPath().startsWith("_")) {
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
        return DocsManager.INSTANCE.getNodeById(id).map(node -> {
            setActivePage(node);
            return true;
        }).orElse(false);
    }

    public boolean scrollToAnchor(String anchorName) {
        if (anchorName.isEmpty()) {
            return false;
        }
        for (Widget w : docsPanel.getWidgets()) {
            if (w instanceof Anchorable a && a.getAnchorName().equals(anchorName)) {
                docsScrollbar.setValue(w.posY);
                return true;
            }
        }

        FTBGuides.LOGGER.warn("unknown anchor #{} in doc {}", anchorName, activeNode.pageId());
        return false;
    }

    private boolean isIndexPinned() {
        return ClientConfig.PINNED.get();
    }

    private class IndexPanel extends Panel {
        private boolean expanded = isIndexPinned();

        public IndexPanel() {
            super(GuideScreen.this);
        }

        @Override
        public void addWidgets() {
            if (activeNode != null && guideIndex != null) {
                guideIndex.categories().forEach(category -> {
                    add(new SubcategoryButton(category));
                    if (!collapsedCategories.contains(category.id())) {
                        DocsManager.INSTANCE.getNodesByCategory(activeNode.pageId().getNamespace(), category.id()).stream()
                                .sorted()
                                .forEach(node -> add(new DocsNodeButton(node)));
                    }
                });
            }
        }

        @Override
        public void alignWidgets() {
            int maxW = 100;

            int hardMax = getScreen().getGuiScaledWidth() / 3;
            for (Widget w : widgets) {
                maxW = Math.min(Math.max(maxW, w.width + w.posX + 5), hardMax);
            }

            setPosAndSize(expanded || isIndexPinned() ? 0 : -maxW, 20, maxW, getGui().height - 21);

            for (Widget w : widgets) {
                w.setX(2 + (w instanceof ListButton lb ? lb.getIndent() : 0));
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

            if (expanded && !isIndexPinned() && !isMouseOver()) {
                setExpanded(false);
            }
        }

        private void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        @Override
        public int getX() {
            return expanded || isIndexPinned() ? 0 : -width;
        }

        @Override
        public void drawBackground(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
            getGuideTheme().indexBgColor().draw(guiGraphics, x, y, w, h);
            getGuideTheme().guiColor().draw(guiGraphics, x + w - 1, y, 1, h);
        }

        private abstract class ListButton extends SimpleTextButton {
            public ListButton(Component title, Icon icon) {
                super(IndexPanel.this, title, icon);
                setHeight(getTheme().getFontHeight() + 4);
            }

            @Override
            public void drawBackground(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
            }

            protected int getIndent() {
                return 0;
            }
        }

        public class DocsNodeButton extends ListButton {
            private final DocsLoader.NodeWithMeta node;

            public DocsNodeButton(DocsLoader.NodeWithMeta node) {
                super(Component.literal(node.metadata().title()), node.metadata().icon());
                this.node = node;
            }

            @Override
            public void onClicked(MouseButton button) {
                setActivePage(node);
                addToHistory("");
            }

            @Override
            public void draw(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
                if (node == activeNode) {
                    getGuideTheme().linkColor().withAlpha(128).draw(guiGraphics, x, y, parent.width - posX - 3, h);
                }
                super.draw(guiGraphics, theme, x, y, w, h);
            }

            @Override
            protected int getIndent() {
                return 12;
            }
        }

        public class SubcategoryButton extends ListButton {
            private final GuideIndex.GuideCategory category;

            public SubcategoryButton(GuideIndex.GuideCategory category) {
                super(makeTitle(collapsedCategories, category, getGuideTheme().codeColor()), category.icon());
                this.category = category;
            }

            @Override
            public void onClicked(MouseButton button) {
                toggleExpanded(category.id());
                indexPanel.refreshWidgets();
            }

            private static Component makeTitle(Set<String> e, GuideIndex.GuideCategory category, Color4I color) {
                Style style = Style.EMPTY.withColor(color.rgba()).withBold(category.isDefault());
                return Component.literal(e.contains(category.id()) ? "▶ " : "▼ ").append(category.name()).withStyle(style);
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
                addAll(DocRenderer.create().parse(activeNode, this));
            }
        }

        @Override
        public void alignWidgets() {
            align(new WidgetLayout.Vertical(0, 4, 0));

//            docsScrollbar.setMaxValue(getContentHeight());
            docsScrollbar.setValue(lastScrollPos);
        }

        @Override
        public void tick() {
            super.tick();

            int prevX = getX();
            int prevW = width;

            int sbWidth = docsScrollbar.shouldDraw() ? docsScrollbar.width : 0;
            setX(indexPanel.expanded || isIndexPinned() ? indexPanel.getX() + indexPanel.width + 5 : 25);
            setWidth(getScreen().getGuiScaledWidth() - docsPanel.getX() - 5 - sbWidth);

            if (prevX != getX() || prevW != width) {
                refreshWidgets();
            }
        }
    }

    private class ToolbarPanel extends Panel {
        private final Button pinButton;
        private final Button searchButton;
        private final SimpleButton closeButton;

        public ToolbarPanel() {
            super(GuideScreen.this);

            pinButton = new SimpleButton(this, Component.empty(), isIndexPinned() ? PIN_ICON_IN : PIN_ICON_OUT, (btn, mb) -> {
                ClientConfig.toggleIndexPinned();
                btn.setIcon(isIndexPinned() ? PIN_ICON_IN : PIN_ICON_OUT);
                if (isIndexPinned()) indexPanel.setExpanded(true);
            });
            searchButton = new SimpleButton(this, Component.empty(), SEARCH_ICON,
                    (btn, mb) -> new SearchScreen(GuideScreen.this).openGui()) {
                @Override
                public void addMouseOverText(TooltipList list) {
                    list.add(Component.translatable("gui.search_box"));
                    list.add(Component.literal("[Ctrl+F]").withStyle(ChatFormatting.DARK_GRAY));
                }
            };
            closeButton = new SimpleButton(this, Component.empty(), Icons.CLOSE,
                    (btn, mb) -> closeGui());
        }

        @Override
        public void addWidgets() {
            add(pinButton);
            add(searchButton);
            add(closeButton);
        }

        @Override
        public void alignWidgets() {
            pinButton.setPosAndSize(2, 2, 16, 16);
            searchButton.setPosAndSize(20, 2, 16, 16);
            closeButton.setPosAndSize(width - 16, 3, 15, 15);
        }

        @Override
        public void drawBackground(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
            getGuideTheme().guiColor().draw(guiGraphics, x, y + h - 1, w, 1);
        }
    }

    private class ExpandIndexButton extends Widget {
        public ExpandIndexButton() {
            super(GuideScreen.this);
        }

        @Override
        public void draw(GuiGraphics guiGraphics, Theme theme, int x, int y, int w, int h) {
            if (!indexPanel.expanded) {
                getGuideTheme().indexBgColor().draw(guiGraphics, x, y, w, h);
                getGuideTheme().guiColor().draw(guiGraphics, x + w, y, 1, h);
                Icons.RIGHT.draw(guiGraphics, x + (w - 12) / 2, y + (h - 12) / 2, 12, 12);
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
