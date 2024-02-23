package dev.ftb.mods.ftbguides.docs;

import dev.ftb.mods.ftbguides.client.gui.widgets.BlockQuoteWidget;
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

        private final List<Widget> widgets = new ArrayList<>();

        private ListHolder listHolder = null;

        private MutableComponent component = Component.empty();
        private final Panel panel;

        public ComponentConverterVisitor(Panel panel) {
            this.panel = panel;
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
            commitComponent(new VerticalSpaceWidget(panel, 4));
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
            commitComponent(new VerticalSpaceWidget(panel, 12));
            // TODO: Move to a horizontal line widget
            commitComponent(new TextField(panel)
                    .setText(Component.literal("-----")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
            );
        }

        @Override
        public void visit(Heading heading) {
            // Commit anything we have so far
            commitComponent();
            if (heading.getLevel() != 1) {
                commitComponent(new VerticalSpaceWidget(panel, 12));
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

            commitComponent(new TextField(panel).setScale(headingScale).setText(component));
            commitComponent(new VerticalSpaceWidget(panel, 12));
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            // Commit anything we have so far
            commitComponent();

            // Visit the children and store the component
            visitChildren(blockQuote);

            // Commit the component with the children's context
            commitComponent(new BlockQuoteWidget(panel, component));
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            commitComponent(new CodeBlockWidget(panel, Arrays.stream(fencedCodeBlock.getLiteral().split("\n")).map(e -> (Component) Component.literal(e)).toList()));
        }

        @Override
        public void visit(IndentedCodeBlock indentedCodeBlock) {
            commitComponent(new CodeBlockWidget(panel, Arrays.stream(indentedCodeBlock.getLiteral().split("\n")).map(e -> (Component) Component.literal(e)).toList()));
        }

        /**
         * Lists are special little snowflakes
         */
        @Override
        public void visit(OrderedList orderedList) {
            commitComponent();
            commitComponent(new VerticalSpaceWidget(panel, 20));
//
//            listHolder = new OrderedListHolder(listHolder, orderedList);
//            visitChildren(orderedList);
//
//            commitComponent(new TextField(panel).setText(component));
//            if (listHolder.getParent() != null) {
//                listHolder = listHolder.getParent();
//            } else {
//                listHolder = null;
//            }
        }

        @Override
        public void visit(BulletList bulletList) {
            commitComponent();
            commitComponent(new VerticalSpaceWidget(panel, 8));
//
            listHolder = new BulletListHolder(listHolder, bulletList);
            visitChildren(bulletList);

            commitComponent(new TextField(panel).setText(component));
//
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
//            commitComponent(); // push any dangling components

//            var beforeComponent = component.copy();
//            component = Component.empty();

            String before = "";
            if (listHolder instanceof OrderedListHolder orderedListHolder) {
                String indent = orderedListHolder.getIndent();

                before = indent + orderedListHolder.getCounter() + orderedListHolder.getDelimiter() + " ";
                visitChildren(listItem);

                orderedListHolder.increaseCounter();
            } else if (listHolder instanceof BulletListHolder bulletListHolder) {
                before = bulletListHolder.getIndent() + bulletListHolder.getMarker() + " ";
                component.append(Component.literal(before));
                visitChildren(listItem);
            }

            commitComponent();
//            component = beforeComponent.append(before).append(component);
        }
        //#endregion

        // TODO: An image can be inline or block, we should support both
        @Override
        public void visit(Image image) {
            commitComponent(new SimpleButton(panel, Component.empty(), Icon.getIcon(image.getDestination()), (simpleButton, mouseButton) -> {}));
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
            visitChildren(paragraph);
            commitComponent();
        }

        private void commitComponent() {
            if (component.equals(Component.empty())) {
                return;
            }

            System.out.println("Committing component (text): " + component.getString());
            widgets.add(new TextField(panel).setText(component));
            component = Component.empty();
        }

        private void commitComponent(TextField field) {
            System.out.println("Committing component (fiel): " + component.getString());
            widgets.add(field);
            component = Component.empty();
        }

        private void commitComponent(Widget widget) {
            System.out.println("Committing widget");
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
}
