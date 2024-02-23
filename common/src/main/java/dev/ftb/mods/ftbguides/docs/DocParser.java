package dev.ftb.mods.ftbguides.docs;

import dev.ftb.mods.ftbguides.client.gui.panel.BlockQuotePanel;
import dev.ftb.mods.ftbguides.client.gui.widgets.CodeBlockWidget;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
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
public class DocParser {
    private DocParser() {
    }

    public static DocParser create() {
        return new DocParser();
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
        private PanelHolder panel;


        public ComponentConverterVisitor(Panel panel) {
            this.panel = new PanelHolder(panel);
        }

        public List<Widget> finish() {
            // Add any dangling components
            if (!component.equals(Component.empty())) {
                commitComponent();
            }

            return widgets;
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
            component.append("\n");
            // TODO: this is wrong but the text widget isn't wrapping properly atm
        }

        //#region inline
        @Override
        public void visit(Link link) {
            var url = link.getDestination();
            var title = link.getTitle();
            var beforeComponent = component.copy();
            component = Component.empty();

            visitChildren(link);

            Style linkStyle = Style.EMPTY.withUnderlined(true).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
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
            component.append(Component.literal(code.getLiteral()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
        //#endregion

        //#region block
        @Override
        public void visit(ThematicBreak thematicBreak) {
            commitComponent();
            commitComponent(new VerticalSpaceWidget(getPanel(), 12));
            // TODO: Move to a horizontal line widget
            commitComponent(new TextField(getPanel())
                    .setText(Component.literal("-----")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
            );
        }

        @Override
        public void visit(Heading heading) {
            // Commit anything we have so far
            // TODO: Spacing should be relative to the heading level
            commitComponent();

            // Don't add space if the previous component was a heading
            if (!(heading.getPrevious() instanceof Heading)) {
                commitComponent(new VerticalSpaceWidget(getPanel(), 12));
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

            commitComponent(new TextField(getPanel()).setScale(headingScale).setText(component));
            commitComponent(new VerticalSpaceWidget(getPanel(), 12));
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            // Commit anything we have so far
            commitComponent();
            commitComponent(new VerticalSpaceWidget(getPanel(), 12));

            // This needs to be a panel or something like that as it needs to group the children and render them in a different way
            this.panel = new PanelHolder(this.panel, new BlockQuotePanel(this.panel.panel));

            // Copy the widget list and reset it
            var previousWidgets = new ArrayList<>(this.widgets);
            this.widgets.clear();

            // Visit the children and store the component
            visitChildren(blockQuote);

            ((BlockQuotePanel) this.panel.panel).setWidgets(new ArrayList<>(this.widgets));

            // Restore the previous widget list
            this.widgets = previousWidgets;

            // Commit the component with the children's context
            commitComponent(this.panel.panel);
            this.panel = this.panel.parent;
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
            commitComponent(new VerticalSpaceWidget(getPanel(), 8));

            listHolder = new OrderedListHolder(listHolder, orderedList);
            visitChildren(orderedList);

            commitComponent(new TextField(getPanel()).setText(component));
            if (listHolder.getParent() != null) {
                listHolder = listHolder.getParent();
            } else {
                listHolder = null;
            }
        }

        @Override
        public void visit(BulletList bulletList) {
            commitComponent();
            commitComponent(new VerticalSpaceWidget(getPanel(), 8));

            listHolder = new BulletListHolder(listHolder, bulletList);
            visitChildren(bulletList);

            commitComponent(new TextField(getPanel()).setText(component));

            if (listHolder.getParent() != null) {
                listHolder = listHolder.getParent();
            } else {
                listHolder = null;
            }
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
                before = bulletListHolder.getIndent() + bulletListHolder.getMarker() + " ";
                component.append(Component.literal(before));
                visitChildren(listItem);
            }

            commitComponent();
        }
        //#endregion

        private Panel getPanel() {
            return this.panel.panel();
        }

        // TODO: An image can be inline or block, we should support both
        @Override
        public void visit(Image image) {
            commitComponent(new SimpleButton(getPanel(), Component.empty(), Icon.getIcon(image.getDestination()), (simpleButton, mouseButton) -> {}));
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
//            commitComponent(new VerticalSpaceWidget(panel, 12));
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
            widgets.add(new TextField(getPanel()).setText(component));
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
}
