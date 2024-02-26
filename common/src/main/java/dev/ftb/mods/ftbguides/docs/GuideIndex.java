package dev.ftb.mods.ftbguides.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftblibrary.icon.Color4I;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record GuideIndex(List<GuideCategory> categories, GuideTheme theme) {
    public static final PrimitiveCodec<Color4I> HEX_COLOR = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Color4I> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).flatMap(GuideIndex::parseColorString);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Color4I value) {
            return ops.createString(value.toString());
        }
    };

    private static DataResult<Color4I> parseColorString(String input) {
        try {
            return DataResult.success(Color4I.fromString(input));
        } catch (NumberFormatException e) {
            return DataResult.error("Invalid hexcolor string: " + input);
        }
    }

    public static final Codec<GuideTheme> THEME_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            HEX_COLOR.optionalFieldOf("background_color", Color4I.BLACK).forGetter(GuideTheme::bgColor),
            HEX_COLOR.optionalFieldOf("index_background_color", Color4I.BLACK).forGetter(GuideTheme::bgColor),
            HEX_COLOR.optionalFieldOf("gui_line_color", Color4I.rgb(606060)).forGetter(GuideTheme::guiColor),
            HEX_COLOR.optionalFieldOf("text_color", Color4I.rgb(0xFFFFFF)).forGetter(GuideTheme::textColor),
            HEX_COLOR.optionalFieldOf("link_color", Color4I.rgb(0x98D9FF)).forGetter(GuideTheme::linkColor),
            HEX_COLOR.optionalFieldOf("code_color", Color4I.rgb(0xEBCB8B)).forGetter(GuideTheme::codeColor)
    ).apply(inst, GuideTheme::new));

    public static final Codec<GuideCategory> CATEGORY_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("id").forGetter(GuideCategory::id),
            Codec.STRING.fieldOf("name").forGetter(GuideCategory::name),
            Codec.STRING.optionalFieldOf("icon").forGetter(GuideCategory::icon)
    ).apply(inst, GuideCategory::new));

    public static final Codec<GuideIndex> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GuideIndex.CATEGORY_CODEC.listOf().fieldOf("categories").forGetter(GuideIndex::categories),
            THEME_CODEC.fieldOf("theme").forGetter(GuideIndex::theme)
    ).apply(inst, GuideIndex::new));

    public Collection<String> knownCategoryIds() {
        return categories.stream().map(GuideCategory::id).toList();
    }

    public record GuideCategory(String id, String name, Optional<String> icon) {
    }

    public record GuideTheme(Color4I bgColor, Color4I indexBgColor, Color4I guiColor, Color4I textColor, Color4I linkColor, Color4I codeColor) {
    }
}
