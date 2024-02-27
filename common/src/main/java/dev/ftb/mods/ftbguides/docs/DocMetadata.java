package dev.ftb.mods.ftbguides.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbguides.client.gui.GuideScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.ftb.mods.ftbguides.docs.GuideIndex.ICON_CODEC;

public record DocMetadata(String title, String categoryId, Icon icon, int order) implements Comparable<DocMetadata> {
    static final String DEFAULT_CATEGORY = "default";

    public static final Codec<DocMetadata> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("title").forGetter(DocMetadata::title),
            Codec.STRING.optionalFieldOf("category", DEFAULT_CATEGORY).forGetter(DocMetadata::categoryId),
            ICON_CODEC.optionalFieldOf("icon", GuideScreen.BLANK_ICON).forGetter(DocMetadata::icon),
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(DocMetadata::order)
    ).apply(inst, DocMetadata::new));

    public static DocMetadata fromReader(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String line = reader.readLine();
        if (line == null || !line.equals("---")) {
            throw new IOException("document must start with '---'!");
        }

        line = reader.readLine();
        while (line != null && !line.equals("---")) {
            headerLines.add(line);
            line = reader.readLine().stripIndent();
        }

        if (headerLines.isEmpty()) {
            throw new IOException("no header found!");
        }

        if (!headerLines.get(0).startsWith("{")) {
            headerLines.add(0, "{");
            headerLines.add("}");
        }

        try {
            CompoundTag tag = SNBT.readLines(headerLines);
            if (tag == null) {
                throw new IOException("invalid header data found: not SNBT");
            }

            return CODEC.parse(NbtOps.INSTANCE, tag).result()
                    .orElseThrow(() -> new IOException("can't parse header data"));
        } catch (SNBTSyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int compareTo(@NotNull DocMetadata other) {
        int n = Integer.compare(order, other.order);
        return n == 0 ? title.compareTo(other.title) : n;
    }
}
