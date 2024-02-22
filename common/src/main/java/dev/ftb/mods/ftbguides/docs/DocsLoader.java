package dev.ftb.mods.ftbguides.docs;

import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DocsLoader extends SimplePreparableReloadListener<Map<ResourceLocation, DocsLoader.NodeWithMeta>> {
    private static final String PATH_SUFFIX = ".md";
    private static final int PATH_SUFFIX_LENGTH = PATH_SUFFIX.length();
    private static final String DIR = "guides";

    @Override
    protected Map<ResourceLocation, NodeWithMeta> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation,NodeWithMeta> map = new HashMap<>();

        String lang = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        String subDir = DIR + "/" + lang;
        int len = subDir.length() + 1;

        Parser parser = Parser.builder().build();

        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(subDir, e -> e.getPath().endsWith(PATH_SUFFIX)).entrySet()) {
            ResourceLocation entryLoc = entry.getKey();
            String path = entryLoc.getPath();
            ResourceLocation resLoc = new ResourceLocation(entryLoc.getNamespace(), path.substring(len, path.length() - PATH_SUFFIX_LENGTH));

            try {
                BufferedReader reader = entry.getValue().openAsReader();
                try {
                    DocMetadata meta = DocMetadata.fromReader(reader);
                    Node node = parser.parseReader(reader);
                    NodeWithMeta prev = map.put(resLoc, new NodeWithMeta(node, meta));
                    if (prev != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + resLoc);
                    }
                } catch (IOException e) {
                    FTBGuides.LOGGER.error("Couldn't parse data file {} from {}: {}", resLoc, entryLoc, e.getMessage());
                }
                reader.close();
            } catch (IOException e) {
                FTBGuides.LOGGER.error("Couldn't get reader for data file {} from {}: {}", resLoc, entryLoc, e.getMessage());
            }
        }

        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, NodeWithMeta> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DocsManager.INSTANCE.rebuildDocs(object);
    }

    public record NodeWithMeta(Node node, DocMetadata metadata) {
    }
}
