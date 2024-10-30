package dev.ftb.mods.ftbguides.docs;

import org.commonmark.node.BulletList;
import org.commonmark.node.OrderedList;

/**
 * We need this because CommonMark's ListHolder class is in a private module, not visible outside dev...
 */
public abstract class CustomListHolder {
    private static final String INDENT_DEFAULT = "   ";
    private static final String INDENT_EMPTY = "";

    private final CustomListHolder parent;
    private final String indent;

    CustomListHolder(CustomListHolder parent) {
        this.parent = parent;

        if (parent != null) {
            indent = parent.indent + INDENT_DEFAULT;
        } else {
            indent = INDENT_EMPTY;
        }
    }

    public CustomListHolder getParent() {
        return parent;
    }

    public String getIndent() {
        return indent;
    }

    public static class Bullet extends CustomListHolder {
        private final String marker;

        public Bullet(CustomListHolder parent, BulletList list) {
            super(parent);
            marker = list.getMarker();
        }

        public String getMarker() {
            return marker;
        }
    }

    public static class Ordered extends CustomListHolder {
        private final String delimiter;
        private int counter;

        public Ordered(CustomListHolder parent, OrderedList list) {
            super(parent);
            delimiter = list.getMarkerDelimiter() != null ? list.getMarkerDelimiter() : ".";
            counter = list.getMarkerStartNumber() != null ? list.getMarkerStartNumber() : 1;
        }

        public String getDelimiter() {
            return delimiter;
        }

        public int getCounter() {
            return counter;
        }

        public void increaseCounter() {
            counter++;
        }
    }
}
