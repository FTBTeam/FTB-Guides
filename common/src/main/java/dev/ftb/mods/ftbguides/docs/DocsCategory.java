package dev.ftb.mods.ftbguides.docs;

import dev.ftb.mods.ftbguides.docs.DocsLoader.NodeWithMeta;

import java.util.*;
import java.util.function.Predicate;

public class DocsCategory {
    private final String name;
    private final List<NodeWithMeta> nodes;
    private final Map<String, DocsCategory> subCategories;

    public DocsCategory(String name, List<NodeWithMeta> nodes, Map<String, DocsCategory> subCategories) {
        this.name = name;
        this.nodes = nodes;
        this.subCategories = subCategories;
    }

    public String getName() {
        return name;
    }

    public static DocsCategory create(String name) {
        return new DocsCategory(name, new ArrayList<>(), new HashMap<>());
    }

    public void visit(Visitor visitor) {
        visitor.visit(name, null);

        Comparator<NodeWithMeta> sorter = Comparator
                .comparingInt((NodeWithMeta n) -> n.metadata().order())
                .thenComparing(n -> n.metadata().title());

        nodes.stream()
                .sorted(sorter)
                .forEach(node -> visitor.visit(name, node));

        subCategories.keySet().stream().sorted().forEach(name -> {
            DocsCategory cat = subCategories.get(name);
            cat.visit(visitor);
        });
    }

    public DocsCategory getOrCreateCategory(String[] path) {
        return path.length == 0 ? this :
                subCategories.computeIfAbsent(path[0], DocsCategory::create)
                        .getOrCreateCategory(Arrays.copyOfRange(path, 1, path.length));

    }

    public void addDoc(NodeWithMeta node) {
        nodes.add(node);
    }

    @FunctionalInterface
    public interface Visitor {
        void visit(String categoryName, NodeWithMeta node);
    }
}
