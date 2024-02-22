package dev.ftb.mods.ftbguides;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class DocsLoader extends SimplePreparableReloadListener<Map<ResourceLocation, Node>> {
    private static final String PATH_SUFFIX = ".md";
    private static final int PATH_SUFFIX_LENGTH = PATH_SUFFIX.length();

    @Override
    protected Map<ResourceLocation, Node> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation,Node> map = new HashMap<>();

        int len = FTBGuides.MOD_ID.length() + 1;

        Parser parser = Parser.builder().build();

        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(FTBGuides.MOD_ID, e -> e.getPath().endsWith(PATH_SUFFIX)).entrySet()) {
            ResourceLocation entryLoc = entry.getKey();
            String path = entryLoc.getPath();
            ResourceLocation resLoc = new ResourceLocation(entryLoc.getNamespace(), path.substring(len, path.length() - PATH_SUFFIX_LENGTH));

            try {
                Reader reader = entry.getValue().openAsReader();
                // TODO slurp in metadata header here
                try {
                    Node node = parser.parseReader(reader);
                    Node prev = map.put(resLoc, node);
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
    protected void apply(Map<ResourceLocation, Node> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {

    }
}
