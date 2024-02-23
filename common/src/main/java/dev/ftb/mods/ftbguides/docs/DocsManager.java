package dev.ftb.mods.ftbguides.docs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.ftb.mods.ftbguides.docs.DocsLoader.NodeWithMeta;
import net.minecraft.resources.ResourceLocation;

import javax.print.Doc;
import java.util.*;
import java.util.function.Predicate;

public enum DocsManager {
    INSTANCE;

    private DocsCategory docRoot;
    private Map<ResourceLocation, NodeWithMeta> byId;
    private Multimap<String, NodeWithMeta> byCategory;

    public void rebuildDocs(Map<ResourceLocation, NodeWithMeta> docs) {
        docRoot = DocsCategory.create("root");
        byId = new HashMap<>();
        byCategory = ArrayListMultimap.create();

        docs.forEach((id, nodeWithMeta) -> {
            String cat = nodeWithMeta.metadata().category();
            String[] catPath = cat.isEmpty() ? new String[0] : cat.split("/");
            DocsCategory target = docRoot.getOrCreateCategory(catPath);
            target.addDoc(nodeWithMeta);
            byId.put(id, nodeWithMeta);
            byCategory.put(target.getName(), nodeWithMeta);
        });
    }

    public Optional<NodeWithMeta> get(ResourceLocation id) {
        return byId == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    public Collection<NodeWithMeta> getByCategory(String category) {
        return byCategory.get(category);
    }

    public Multimap<String,NodeWithMeta> getCategories() {
        return byCategory;
    }

    public void visit(DocsCategory.Visitor visitor) {
        if (docRoot != null) {
            docRoot.visit(visitor);
        }
    }
}
