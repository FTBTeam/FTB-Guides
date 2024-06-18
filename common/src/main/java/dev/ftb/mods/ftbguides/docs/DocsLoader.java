package dev.ftb.mods.ftbguides.docs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.architectury.platform.Platform;
import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.commonmark.Extension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocsLoader extends SimplePreparableReloadListener<DocsLoader.RawGuideData> {
    private static final String PATH_SUFFIX = ".md";
    private static final int PATH_SUFFIX_LENGTH = PATH_SUFFIX.length();
    private static final String DIR = "guides";
    private static final String INDEX = "guide.json";
    private static final Gson gson = new GsonBuilder().create();

    private static final List<Extension> EXTENSIONS = List.of(ImageAttributesExtension.create());

    @Override
    protected RawGuideData prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        RawGuideData rawGuideData = new RawGuideData(new HashMap<>(), new HashMap<>());

        String lang = Minecraft.getInstance().getLanguageManager().getSelected();
        String subDir = DIR + "/" + lang;

        Map<ResourceLocation, Resource> indexMap = resourceManager.listResources(subDir, e -> e.getPath().endsWith(INDEX));
        if (indexMap.isEmpty()) {
            if (!lang.equals("en_us")) {
                // try fallback language
                subDir = DIR + "/en_us";
                indexMap = resourceManager.listResources(subDir, e -> e.getPath().endsWith(INDEX));
            }
            if (indexMap.isEmpty()) {
                FTBGuides.LOGGER.error("no guide data found!");
                return rawGuideData;
            }
        }

        indexMap.forEach((entryLoc, value) -> loadGuideJsonFile(entryLoc, value, rawGuideData));

        int len = subDir.length() + 1;

        Parser parser = Parser.builder().extensions(EXTENSIONS).build();

        resourceManager.listResources(subDir, e -> e.getPath().endsWith(PATH_SUFFIX))
                .forEach((entryLoc, resource) ->
                        loadMarkdownFile(entryLoc, resource, len, parser, rawGuideData));

        return rawGuideData;
    }

    private static void loadMarkdownFile(ResourceLocation entryLoc, Resource value, int len, Parser parser, RawGuideData rawGuideData) {
        if (shouldIgnore(entryLoc)) return;

        String path = entryLoc.getPath();
        ResourceLocation resLoc = ResourceLocation.fromNamespaceAndPath(entryLoc.getNamespace(), path.substring(len, path.length() - PATH_SUFFIX_LENGTH));
        try {
            BufferedReader reader = value.openAsReader();
            try {
                DocMetadata meta = DocMetadata.fromReader(reader);
                Node node = parser.parseReader(reader);
                NodeWithMeta prev = rawGuideData.pages().put(resLoc, new NodeWithMeta(resLoc, node, meta));
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

    private static void loadGuideJsonFile(ResourceLocation entryLoc, Resource value, RawGuideData rawGuideData) {
        if (shouldIgnore(entryLoc)) return;
        try {
            Reader reader = value.openAsReader();
            try {
                JsonElement jsonElement = GsonHelper.fromJson(gson, reader, JsonElement.class);
                rawGuideData.indexes().put(entryLoc.getNamespace(),
                        GuideIndex.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(err -> {
                            throw new IllegalStateException("Failed to parse guide.json file " + entryLoc + ": " + err);
                        }));
            } catch (Throwable e) {
                try {
                    reader.close();
                } catch (Throwable e2) {
                    e.addSuppressed(e2);
                }
                throw e;
            }
        } catch (RuntimeException | IOException e) {
            FTBGuides.LOGGER.error("Couldn't parse guide.json file {}: {}", entryLoc, e);
        }
    }

    private static boolean shouldIgnore(ResourceLocation entryLoc) {
        return entryLoc.getNamespace().startsWith("ftbguides_dev") && !Platform.isDevelopmentEnvironment();
    }

    @Override
    protected void apply(RawGuideData rawGuideData, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DocsManager.INSTANCE.rebuildDocs(rawGuideData);
    }

    public record RawGuideData(Map<String,GuideIndex> indexes, Map<ResourceLocation,NodeWithMeta> pages) {
    }

    public record NodeWithMeta(ResourceLocation pageId, Node node, DocMetadata metadata) implements Comparable<NodeWithMeta> {
        @Override
        public int compareTo(@NotNull DocsLoader.NodeWithMeta other) {
            return metadata.compareTo(other.metadata);
        }
    }
}
