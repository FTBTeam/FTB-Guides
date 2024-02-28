package dev.ftb.mods.ftbguides.docs;

import dev.ftb.mods.ftbguides.client.gui.GuideThemeProvider;
import dev.ftb.mods.ftbguides.client.gui.panel.BlockQuotePanel;
import dev.ftb.mods.ftbguides.client.gui.widgets.CodeBlockWidget;
import dev.ftb.mods.ftbguides.client.gui.widgets.CustomTextField;
import dev.ftb.mods.ftbguides.client.gui.widgets.IconButton;
import dev.ftb.mods.ftbguides.client.gui.widgets.LineBreakWidget;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.math.PixelBuffer;
import dev.ftb.mods.ftblibrary.ui.*;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.network.chat.*;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.ext.image.attributes.ImageAttributes;
import org.commonmark.internal.renderer.text.BulletListHolder;
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.internal.renderer.text.OrderedListHolder;
import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Parses a markdown file into a list of components. Contains support for parsing frontmatter like headers
 */
public class DocRenderer {
    private DocRenderer() {
    }

    public static DocRenderer create() {
        return new DocRenderer();
    }

    public List<Widget> parse(Node node, Panel panel) {
        var visitor = new ComponentConverterVisitor(panel);
        node.accept(visitor);

        return visitor.finish();
    }

    private static class ComponentConverterVisitor extends AbstractVisitor implements NodeRenderer {
        private static final Logger LOGGER = LoggerFactory.getLogger(ComponentConverterVisitor.class);

        private List<Widget> widgets = new ArrayList<>();

        private ListHolder listHolder = null;

        private MutableComponent component = Component.empty();
        private PanelHolder panelHolder;

        public ComponentConverterVisitor(Panel panel) {
            this.panelHolder = new PanelHolder(panel);
        }

        public List<Widget> finish() {
            // Add any dangling components
            if (!component.equals(Component.empty())) {
                commitComponent();
            }

            // a little spacing right at the end looks better
            commitComponent(new VerticalSpaceWidget(getPanel(), 8));

            return widgets;
        }

        private GuideIndex.GuideTheme getGuideTheme() {
            return getPanel().getGui() instanceof GuideThemeProvider prov ?
                    prov.getGuideTheme() :
                    GuideIndex.GuideTheme.FALLBACK;
        }

        @Override
        public void visit(Text text) {
            component.append(text.getLiteral());
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            commitComponent(new VerticalSpaceWidget(getPanel(), 4));
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            // here we just join lines with a space, and let the TextField do the wrapping
            component.append(" ");
        }

        //#region inline
        @Override
        public void visit(Link link) {
            var url = link.getDestination();
            var title = link.getTitle();
            var beforeComponent = component.copy();
            component = Component.empty();

            visitChildren(link);

            Style linkStyle = Style.EMPTY.withUnderlined(true)
                    .withColor(TextColor.fromRgb(getGuideTheme().linkColor().rgb()))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
            if (title != null) {
                linkStyle = linkStyle.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(title)));
            }

            component = beforeComponent.append(component.withStyle(linkStyle));
        }

        @Override
        public void visit(Emphasis emphasis) {
            // Store the current component, reset it for the children, then restore it after and append the new component
            var beforeComponent = component.copy();
            component = Component.empty();

            this.visitChildren(emphasis);
            component = beforeComponent.append(component.withStyle(Style.EMPTY.withItalic(true)));
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            var beforeComponent = component.copy();
            component = Component.empty();

            this.visitChildren(strongEmphasis);
            component = beforeComponent.append(component.withStyle(Style.EMPTY.withBold(true)));
        }

        @Override
        public void visit(Code code) {
            component.append(Component.literal(code.getLiteral())
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(getGuideTheme().codeColor().rgb())))
            );
        }
        //#endregion

        //#region block
        @Override
        public void visit(ThematicBreak thematicBreak) {
            commitComponent();

            commitComponent(new LineBreakWidget(getPanel()));
        }

        @Override
        public void visit(Heading heading) {
            // Commit anything we have so far
            commitComponent();

            // Don't add space if the previous component was a heading, or this is the first component
            if (heading.getPrevious() != null && !(heading.getPrevious() instanceof Heading)) {
                commitComponent(new VerticalSpaceWidget(getPanel(), 4));
            }

            visitChildren(heading);

            // Make the heading bold if it's level 5 or higher
            if (heading.getLevel() > 4) {
                component = component.setStyle(Style.EMPTY.withBold(true));
            }

            float headingScale = 1f;
            if (heading.getLevel() == 1) {
                headingScale = 2.2f;
            } else if (heading.getLevel() == 2) {
                headingScale = 1.5f;
            } else if (heading.getLevel() == 3) {
                headingScale = 1.2f;
            }

            CustomTextField field = (CustomTextField) new CustomTextField(getPanel(), component).setScale(headingScale);
            field.setAnchorName(component.getString().toLowerCase(Locale.ROOT).replace(' ', '-'));
            field.setHeight((int) (field.height * headingScale) + 2);
            field.setWidth((int) (field.width * headingScale) + 2);
            commitComponent(field);
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            // Commit anything we have so far
            commitComponent();

            // This needs to be a panel or something like that as it needs to group the children and render them in a different way
            int w = getPanel().width;
            this.panelHolder = new PanelHolder(this.panelHolder, new BlockQuotePanel(getPanel()));
            getPanel().setWidth(w);

            // Copy the widget list and reset it
            var previousWidgets = new ArrayList<>(this.widgets);
            this.widgets.clear();

            // Visit the children and store the component
            visitChildren(blockQuote);

            if (getPanel() instanceof BlockQuotePanel bqp) {
                bqp.setWidgets(List.copyOf(widgets));
            }

            // Restore the previous widget list
            this.widgets = previousWidgets;

            // Commit the component with the children's context
            commitComponent(getPanel());

            this.panelHolder = this.panelHolder.parent;
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            commitComponent(new CodeBlockWidget(getPanel(), Arrays.stream(fencedCodeBlock.getLiteral().split("\n")).map(e -> (Component) Component.literal(e)).toList()));
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            commitComponent(new CodeBlockWidget(getPanel(), Arrays.stream(indentedCodeBlock.getLiteral().split("\n")).map(e -> (Component) Component.literal(e)).toList()));
        }

        @Override
        public void visit(OrderedList orderedList) {
            commitComponent();

            if (!(orderedList.getParent() instanceof ListItem)) {
                commitComponent(new VerticalSpaceWidget(getPanel(), 4));
            }

            listHolder = new OrderedListHolder(listHolder, orderedList);
            visitChildren(orderedList);

            if (listHolder.getParent() != null) {
                listHolder = listHolder.getParent();
            } else {
                listHolder = null;
            }

            if (!(orderedList.getParent() instanceof ListItem)) {
                commitComponent(new VerticalSpaceWidget(getPanel(), 4));
            }
        }

        @Override
        public void visit(BulletList bulletList) {
            commitComponent();

            if (!(bulletList.getParent() instanceof ListItem)) {
                commitComponent(new VerticalSpaceWidget(getPanel(), 4));
            }

            listHolder = new BulletListHolder(listHolder, bulletList);
            visitChildren(bulletList);

            if (listHolder.getParent() != null) {
                listHolder = listHolder.getParent();
            } else {
                listHolder = null;
            }
            if (!(bulletList.getParent() instanceof ListItem))
                commitComponent(new VerticalSpaceWidget(getPanel(), 4));
        }

        /**
         * An item is interesting as it builds on from its parent list and thus isn't inherently a block
         */
        @Override
        public void visit(ListItem listItem) {
            String before;
            if (listHolder instanceof OrderedListHolder orderedListHolder) {
                String indent = orderedListHolder.getIndent();

                before = indent + orderedListHolder.getCounter() + orderedListHolder.getDelimiter() + " ";
                component.append(Component.literal(before));
                visitChildren(listItem);

                orderedListHolder.increaseCounter();
            } else if (listHolder instanceof BulletListHolder bulletListHolder) {
                before = bulletListHolder.getIndent() + "• ";
                component.append(Component.literal(before));
                visitChildren(listItem);
            }

            commitComponent();
        }
        //#endregion

        private Panel getPanel() {
            return this.panelHolder.panel();
        }

        // TODO: An image can be inline or block, we should support both
        @Override
        public void visit(Image image) {
            // kludge here: FTB Library requires space after ";" property separators
            // but markdown will not accept spaces in image links
            String s = image.getDestination()
                    .replaceAll(";(\\w)", "; $1")
                    .replaceAll("\\+", " + ");
            Icon icon = Icon.getIcon(s);

            int w = 16, h = 16;
            // TODO find a way of specifying alignment (image attributes only allows width & height)
            ImageAlign align = ImageAlign.LEFT;

            if (icon.hasPixelBuffer()) {
                PixelBuffer buf = icon.createPixelBuffer();
                if (buf != null) {
                    w = buf.getWidth();
                    h = buf.getHeight();
                }
            }
            if (image.getFirstChild().getNext() instanceof ImageAttributes attr) {
                IntIntPair pair = getImageDimensions(attr, w, h);
                w = pair.leftInt();
                h = pair.rightInt();
            }

            Component title = image.getTitle() == null ? Component.empty() : Component.literal(image.getTitle());
            SimpleButton b = new IconButton(getPanel(), title, icon, (simpleButton, mouseButton) -> {});
            b.setSize(w, h);
            b.setX(align.getAlignX(getPanel().width, w));

            commitComponent(b);
        }

        private IntIntPair getImageDimensions(ImageAttributes attr, int curWidth, int curHeight) {
            // preserve aspect ratio here, if possible
            OptionalInt w = getIntAttr(attr, "width");
            OptionalInt h = getIntAttr(attr, "height");
            if (w.isPresent() && h.isPresent()) {
                return IntIntPair.of(w.getAsInt(), h.getAsInt());
            } else if (w.isPresent()) {
                return IntIntPair.of(w.getAsInt(), (curHeight * w.getAsInt()) / curWidth);
            } else if (h.isPresent()) {
                return IntIntPair.of((curWidth * h.getAsInt()) / curHeight, h.getAsInt());
            } else {
                //noinspection SuspiciousNameCombination
                return IntIntPair.of(curWidth, curHeight);
            }
        }

        private OptionalInt getIntAttr(ImageAttributes attr, String field) {
            String s = attr.getAttributes().getOrDefault(field, "");
            return StringUtils.isNumeric(s) ? OptionalInt.of(Integer.parseInt(s)) : OptionalInt.empty();
        }

        @Override
        public void visit(CustomNode customNode) {
            // TODO: Implement support for color nodes
            super.visit(customNode);
        }

        @Override
        public void visit(CustomBlock customBlock) {
            // TODO: Implement support for color blocks
            super.visit(customBlock);
        }

        @Override
        public void visit(Paragraph paragraph) {
            if (paragraph.getPrevious() instanceof Paragraph) {
                component.append(Component.literal("\n"));
            }
            visitChildren(paragraph);
            commitComponent();
        }

        private void commitComponent() {
            if (component.equals(Component.empty())) {
                return;
            }

//            System.out.println("Committing component (text): " + component.getString());
            widgets.add(new CustomTextField(getPanel(), component));
            component = Component.empty();
        }

        private void commitComponent(TextField field) {
//            System.out.println("Committing component (fiel): " + component.getString());
            widgets.add(field);
            component = Component.empty();
        }

        private void commitComponent(Widget widget) {
//            System.out.println("Committing widget");
            widgets.add(widget);
            component = Component.empty();
        }

        @Override
        public void visit(HtmlInline htmlInline) {
            LOGGER.warn("HTML is not supported");
            // NOPE!
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            LOGGER.warn("HTML is not supported");
            // NOPE!
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return new HashSet<>(Arrays.asList(
                    Document.class,
                    Heading.class,
                    Paragraph.class,
                    BlockQuote.class,
                    BulletList.class,
                    FencedCodeBlock.class,
                    ThematicBreak.class,
                    IndentedCodeBlock.class,
                    Link.class,
                    ListItem.class,
                    OrderedList.class,
                    Image.class,
                    Emphasis.class,
                    StrongEmphasis.class,
                    Text.class,
                    Code.class,
                    SoftLineBreak.class,
                    HardLineBreak.class
            ));
        }

        @Override
        public void render(Node node) {
            node.accept(this);
        }
    }

    record PanelHolder(@Nullable PanelHolder parent, Panel panel) {
        public PanelHolder(Panel parent) {
            this(null, parent);
        }
    }

    enum ImageAlign {
        LEFT,
        CENTER,
        RIGHT;

        private static final Map<String,ImageAlign> MAP = Map.of("left", LEFT, "center", CENTER, "right", RIGHT);

        public static ImageAlign create(String what) {
            return MAP.getOrDefault(what.toLowerCase(Locale.ROOT), LEFT);
        }

        public int getAlignX(int panelWidth, int imgWidth) {
            return switch (this) {
                case LEFT -> 0;
                case CENTER -> (panelWidth - imgWidth) / 2;
                case RIGHT -> panelWidth - imgWidth;
            };
        }
    }
}
