package dev.ftb.mods.ftbguides.docs;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum DocsManager {
    INSTANCE;

    private DocsCategory docRoot;
    private Map<ResourceLocation, DocsLoader.NodeWithMeta> byId;

    public void rebuildDocs(Map<ResourceLocation, DocsLoader.NodeWithMeta> docs) {
        docRoot = DocsCategory.create("root");
        byId = new HashMap<>();

        docs.forEach((id, nodeWithMeta) -> {
            String[] catPath = nodeWithMeta.metadata().category().split("/");
            docRoot.getOrCreateCategory(catPath).addDoc(nodeWithMeta);
            byId.put(id, nodeWithMeta);
        });
    }

    public Optional<DocsLoader.NodeWithMeta> get(ResourceLocation id) {
        return byId == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    public void visit(DocsCategory.Visitor visitor) {
        if (docRoot != null) {
            docRoot.visit(visitor);
        }
    }
}
