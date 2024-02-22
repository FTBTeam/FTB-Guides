package dev.ftb.mods.ftbguides.docs;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbguides.widgets.BlockQuoteWidget;
import dev.ftb.mods.ftbguides.widgets.CodeBlockWidget;
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
import java.util.function.Consumer;

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
                widgets.add(new TextField(panel).setText(component));
            }

            return widgets;
        }

        @Override
        public void visit(Text text) {
            component.append(text.getLiteral());
        }

        @Override
        public void visit(Paragraph paragraph) {
            visitChildren(paragraph);
//            commitComponent();
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            widgets.add(new VerticalSpaceWidget(panel, 4));
            commitComponent();
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            component.append("\n");
        }

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
        public void visit(ThematicBreak thematicBreak) {
            // TODO: Move to a horizontal line widget
            widgets.add(new TextField(panel)
                    .setText(Component.literal("-----")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
            );

            commitComponent();
        }

        @Override
        public void visit(Heading heading) {
            var beforeComponent = component.copy();
            component = Component.empty();

            visitChildren(heading);

            if (heading.getLevel() > 4) {
                component = beforeComponent.append(component).setStyle(Style.EMPTY.withBold(true));
            } else {
                component = beforeComponent.append(component);
            }

            commitComponent(new TextField(panel).setScale((4 - heading.getLevel()) * .5F).setText(component));
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            var beforeComponent = component.copy();
            component = Component.empty();

            visitChildren(blockQuote);
            commitComponent(new BlockQuoteWidget(panel, beforeComponent.append(component)));
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
            // We might want to add a new line here, but it's not necessary
//            if (listHolder != null) {
//                writeEndOfLine();
//            }
            commitComponent();
            commitComponent(new VerticalSpaceWidget(panel, 2));

            var beforeComponent = component.copy();
            component = Component.empty();

            listHolder = new OrderedListHolder(listHolder, orderedList);
            visitChildren(orderedList);

            commitComponent(new TextField(panel).setText(beforeComponent.append(component)));
            if (listHolder.getParent() != null) {
                listHolder = listHolder.getParent();
            } else {
                listHolder = null;
            }
        }

        @Override
        public void visit(BulletList bulletList) {
            // We might want to add a new line here, but it's not necessary
//            if (listHolder != null) {
//                writeEndOfLine();
//            }
            commitComponent();
            commitComponent(new VerticalSpaceWidget(panel, 2));

            var beforeComponent = component.copy();
            component = Component.empty();

            listHolder = new BulletListHolder(listHolder, bulletList);
            visitChildren(bulletList);

            commitComponent(new TextField(panel).setText(beforeComponent.append(component)));
            if (listHolder.getParent() != null) {
                listHolder = listHolder.getParent();
            } else {
                listHolder = null;
            }
        }

        @Override
        public void visit(ListItem listItem) {
            if (listHolder instanceof OrderedListHolder orderedListHolder) {
                String indent = orderedListHolder.getIndent();

                var beforeComponent = component.copy();
                component = Component.empty();
                var before = indent + orderedListHolder.getCounter() + orderedListHolder.getDelimiter() + " ";
                visitChildren(listItem);

                component = beforeComponent.append(before).append(component).append("\n");
                orderedListHolder.increaseCounter();
            } else if (listHolder instanceof BulletListHolder bulletListHolder) {
                var before = bulletListHolder.getIndent() + bulletListHolder.getMarker() + " ";
                var beforeComponent = component.copy();
                component = Component.empty();
                visitChildren(listItem);

                component = beforeComponent.append(before).append(component).append("\n");
            }
        }

        @Override
        public void visit(Image image) {
            commitComponent(new SimpleButton(panel, Component.empty(), Icon.getIcon(image.getDestination()), (simpleButton, mouseButton) -> {}));
        }

        @Override
        public void visit(Code code) {
            pushComponent(code.getLiteral(), Style.EMPTY.withColor(ChatFormatting.GOLD));
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

        private void pushComponent(String text, @Nullable Style style) {
            if (style != null) {
                component.append(Component.literal(text).withStyle(style));
            } else {
                component.append(text);
            }
        }

        private void commitComponent() {
            widgets.add(new TextField(panel).setText(component));
            component = Component.empty();
        }

        private void commitComponent(TextField field) {
            widgets.add(field/*.setText(component)*/);
            component = Component.empty();
        }

        private void commitComponent(Widget widget) {
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
