package dev.ftb.mods.ftbguides.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbguides.client.gui.GuideScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import static dev.ftb.mods.ftbguides.docs.GuideIndex.ICON_CODEC;

public record DocMetadata(String title, String categoryId, Icon icon, int order, List<String> tags, List<String> hiddenTags) implements Comparable<DocMetadata> {
    static final String DEFAULT_CATEGORY = "default";
    private static final Pattern TAG_PAT = Pattern.compile("^[a-zA-Z0-9]*$");

    public static Codec<String> tagCodec() {
        Function<String,DataResult<String>> checker = s -> TAG_PAT.matcher(s).matches() ?
                DataResult.success(s) :
                DataResult.error(() -> "Value " + s + " is not alphanumeric!");
        return Codec.STRING.flatXmap(checker, checker);
    }

    public static final Codec<DocMetadata> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("title").forGetter(DocMetadata::title),
            Codec.STRING.optionalFieldOf("category", DEFAULT_CATEGORY).forGetter(DocMetadata::categoryId),
            ICON_CODEC.optionalFieldOf("icon", GuideScreen.BLANK_ICON).forGetter(DocMetadata::icon),
            Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(DocMetadata::order),
            tagCodec().listOf().optionalFieldOf("tags", List.of()).forGetter(DocMetadata::tags),
            tagCodec().listOf().optionalFieldOf("hidden_tags", List.of()).forGetter(DocMetadata::hiddenTags)
    ).apply(inst, DocMetadata::new));

    public static DocMetadata searchResult(String res) {
        return new DocMetadata(res, "_search", Icon.empty(), 0, List.of(), List.of());
    }

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
