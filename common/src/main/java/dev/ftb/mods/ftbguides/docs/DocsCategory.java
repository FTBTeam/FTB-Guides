package dev.ftb.mods.ftbguides.docs;

import java.util.*;

public class DocsCategory {
    private final String name;
    private final List<DocsLoader.NodeWithMeta> nodes;
    private final Map<String, DocsCategory> subCategories;

    public DocsCategory(String name, List<DocsLoader.NodeWithMeta> nodes, Map<String, DocsCategory> subCategories) {
        this.name = name;
        this.nodes = nodes;
        this.subCategories = subCategories;
    }

    public static DocsCategory create(String name) {
        return new DocsCategory(name, new ArrayList<>(), new HashMap<>());
    }

    public void visit(Visitor visitor) {
        nodes.forEach(node -> visitor.visit(name, node));

        subCategories.forEach((name, cat) -> cat.visit(visitor));
    }

    public DocsCategory getOrCreateCategory(String[] path) {
        return path.length == 0 ? this :
                subCategories.computeIfAbsent(path[0], DocsCategory::create)
                        .getOrCreateCategory(Arrays.copyOfRange(path, 1, path.length));

    }

    public void addDoc(DocsLoader.NodeWithMeta node) {
        nodes.add(node);
    }

    @FunctionalInterface
    public interface Visitor {
        void visit(String categoryName, DocsLoader.NodeWithMeta node);
    }
}
