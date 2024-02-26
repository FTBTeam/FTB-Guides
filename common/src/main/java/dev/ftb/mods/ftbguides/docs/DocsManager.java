package dev.ftb.mods.ftbguides.docs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.docs.DocsLoader.NodeWithMeta;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public enum DocsManager {
    INSTANCE;

    private DocsCategory docRoot;
    private Map<ResourceLocation, NodeWithMeta> byId;
    private Multimap<String, NodeWithMeta> byCategory;
    private Map<String, GuideIndex> indexMap;

    public void rebuildDocs(DocsLoader.GuideBook guideBook) {
        docRoot = DocsCategory.create("");
        byId = new HashMap<>();
        byCategory = ArrayListMultimap.create();
        indexMap = Map.copyOf(guideBook.indexes());

        Map<String, Set<String>> knownCategories = new HashMap<>();
        indexMap.forEach((namespace, guideIndex) ->
                knownCategories.put(namespace, Set.copyOf(guideIndex.knownCategoryIds()))
        );

        for (Map.Entry<ResourceLocation, NodeWithMeta> entry : guideBook.pages().entrySet()) {
            ResourceLocation id = entry.getKey();
            NodeWithMeta nodeWithMeta = entry.getValue();
            String cat = nodeWithMeta.metadata().category();
            if (!knownCategories.containsKey(id.getNamespace())) {
                FTBGuides.LOGGER.error("no index found for doc {}, skipping page", id);
                continue;
            }
            if (!knownCategories.get(id.getNamespace()).contains(cat)) {
                FTBGuides.LOGGER.error("category {} not defined in guide.json for doc {}, skipping page", cat, id);
                continue;
            }
            String[] catPath = cat.isEmpty() ? new String[0] : cat.split("/");
            DocsCategory target = docRoot.getOrCreateCategory(catPath);
            target.addDoc(nodeWithMeta);
            byId.put(id, nodeWithMeta);
            byCategory.put(target.getName(), nodeWithMeta);
        }
    }

    public Optional<NodeWithMeta> get(ResourceLocation id) {
        if (id.getPath().startsWith("/")) {
            id = new ResourceLocation(id.getNamespace(), id.getPath().substring(1));
        }
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

    public GuideIndex getIndex(String namespace) {
        return indexMap.get(namespace);
    }
}
