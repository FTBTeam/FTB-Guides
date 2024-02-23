package dev.ftb.mods.ftbguides.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record DocMetadata(String title, String category, Optional<String> icon, int order) {
    public static final Codec<DocMetadata> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("title").forGetter(DocMetadata::title),
            Codec.STRING.optionalFieldOf("category", "").forGetter(DocMetadata::category),
            Codec.STRING.optionalFieldOf("icon").forGetter(DocMetadata::icon),
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
            line = reader.readLine();
        }

        if (headerLines.isEmpty()) {
            throw new IOException("no header found!");
        }

        CompoundTag tag = SNBT.readLines(headerLines);
        if (tag == null) {
            throw new IOException("invalid header data found: not SNBT");
        }

        return CODEC.parse(NbtOps.INSTANCE, tag).result()
                .orElseThrow(() -> new IOException("can't parse header data"));
    }

    public Icon makeIcon() {
        return icon().map(Icon::getIcon).orElse(Color4I.BLACK.withAlpha(0));
    }
}
