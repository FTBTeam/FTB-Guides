package dev.ftb.mods.ftbguides.docs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class DocsLoader extends SimplePreparableReloadListener<DocsLoader.GuideBook> {
    private static final String PATH_SUFFIX = ".md";
    private static final int PATH_SUFFIX_LENGTH = PATH_SUFFIX.length();
    private static final String DIR = "guides";
    private static final String INDEX = "guide.json";
    private static final Gson gson = new GsonBuilder().create();

    @Override
    protected GuideBook prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        GuideBook guideBook = new GuideBook(new HashMap<>(), new HashMap<>());

        String lang = Minecraft.getInstance().getLanguageManager().getSelected().getCode();
        String subDir = DIR + "/" + lang;
        int len = subDir.length() + 1;

        Parser parser = Parser.builder().build();

        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(subDir, e -> e.getPath().endsWith(INDEX)).entrySet()) {
            ResourceLocation entryLoc = entry.getKey();
            String path = entryLoc.getPath();
            try {
                Reader reader = entry.getValue().openAsReader();
                try {
                    JsonElement jsonElement = GsonHelper.fromJson(gson, reader, JsonElement.class);
                    guideBook.indexes().put(entryLoc.getNamespace(),
                            GuideIndex.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(false, err -> {}));
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

        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(subDir, e -> e.getPath().endsWith(PATH_SUFFIX)).entrySet()) {
            ResourceLocation entryLoc = entry.getKey();
            String path = entryLoc.getPath();
            ResourceLocation resLoc = new ResourceLocation(entryLoc.getNamespace(), path.substring(len, path.length() - PATH_SUFFIX_LENGTH));

            try {
                BufferedReader reader = entry.getValue().openAsReader();
                try {
                    DocMetadata meta = DocMetadata.fromReader(reader);
                    Node node = parser.parseReader(reader);
                    NodeWithMeta prev = guideBook.pages().put(resLoc, new NodeWithMeta(resLoc, node, meta));
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

        return guideBook;
    }

    @Override
    protected void apply(GuideBook guideBook, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DocsManager.INSTANCE.rebuildDocs(guideBook);
    }

    public record GuideBook(Map<String,GuideIndex> indexes, Map<ResourceLocation,NodeWithMeta> pages) {
    }

    public record NodeWithMeta(ResourceLocation pageId, Node node, DocMetadata metadata) {
    }
}
