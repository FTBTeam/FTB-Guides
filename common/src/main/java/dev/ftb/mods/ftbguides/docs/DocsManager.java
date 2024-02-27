package dev.ftb.mods.ftbguides.docs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.docs.DocsLoader.NodeWithMeta;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public enum DocsManager {
    INSTANCE;

    private Map<ResourceLocation, NodeWithMeta> byId;
    private Multimap<ResourceLocation, NodeWithMeta> byCategory;
    private Map<String, GuideIndex> indexMap;

    public void rebuildDocs(DocsLoader.RawGuideData rawGuideData) {
        byId = new HashMap<>();
        byCategory = ArrayListMultimap.create();
        indexMap = Map.copyOf(rawGuideData.indexes());

        Set<ResourceLocation> knownCats = new HashSet<>();
        indexMap.forEach((namespace, guideIndex) ->
                guideIndex.knownCategoryIds().stream()
                        .map(c -> new ResourceLocation(namespace, c))
                        .forEach(knownCats::add)
        );

        for (Map.Entry<ResourceLocation, NodeWithMeta> entry : rawGuideData.pages().entrySet()) {
            ResourceLocation pageId = entry.getKey();
            NodeWithMeta nodeWithMeta = entry.getValue();
            String catId = nodeWithMeta.metadata().categoryId();

            if (!knownCats.contains(new ResourceLocation(pageId.getNamespace(), catId))) {
                FTBGuides.LOGGER.error("category {} not defined in guide.json for doc {}, skipping page", catId, pageId);
                continue;
            }

            byId.put(pageId, nodeWithMeta);
            byCategory.put(new ResourceLocation(pageId.getNamespace(), catId), nodeWithMeta);
        }
    }

    public Optional<NodeWithMeta> getNodeById(ResourceLocation id) {
        if (id.getPath().startsWith("/")) {
            id = new ResourceLocation(id.getNamespace(), id.getPath().substring(1));
        }
        return byId == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    public Collection<NodeWithMeta> getNodesByCategory(String namespace, String categoryId) {
        return byCategory.get(new ResourceLocation(namespace, categoryId));
    }

    public GuideIndex getIndex(String namespace) {
        return indexMap.get(namespace);
    }
}
