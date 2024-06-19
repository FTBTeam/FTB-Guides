package dev.ftb.mods.ftbguides.docs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbguides.client.gui.GuideScreen;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;

import java.util.Collection;
import java.util.List;

public record GuideIndex(List<GuideCategory> categories, GuideTheme theme) {
    public static final Codec<Color4I> COLOR4I_CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Color4I> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).flatMap(GuideIndex::parseColorString);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Color4I value) {
            return ops.createString(value.toString());
        }
    };

    public static final Codec<Icon> ICON_CODEC = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Icon> read(DynamicOps<T> ops, T input) {
            return ops.getStringValue(input).flatMap(GuideIndex::parseIconString);
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Icon value) {
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

    private static DataResult<Icon> parseIconString(String input) {
        return DataResult.success(Icon.getIcon(input));
    }

    public static final Codec<GuideTheme> THEME_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            COLOR4I_CODEC.optionalFieldOf("background_color", Color4I.BLACK.withAlpha(128)).forGetter(GuideTheme::bgColor),
            COLOR4I_CODEC.optionalFieldOf("index_background_color", Color4I.DARK_GRAY).forGetter(GuideTheme::bgColor),
            COLOR4I_CODEC.optionalFieldOf("gui_line_color", Color4I.rgb(606060)).forGetter(GuideTheme::guiColor),
            COLOR4I_CODEC.optionalFieldOf("text_color", Color4I.rgb(0xFFFFFF)).forGetter(GuideTheme::textColor),
            COLOR4I_CODEC.optionalFieldOf("link_color", Color4I.rgb(0x98D9FF)).forGetter(GuideTheme::linkColor),
            COLOR4I_CODEC.optionalFieldOf("code_color", Color4I.rgb(0xEBCB8B)).forGetter(GuideTheme::codeColor)
    ).apply(inst, GuideTheme::new));

    public static final Codec<GuideCategory> CATEGORY_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("id").forGetter(GuideCategory::id),
            Codec.STRING.fieldOf("name").forGetter(GuideCategory::name),
            ICON_CODEC.optionalFieldOf("icon", GuideScreen.BLANK_ICON).forGetter(GuideCategory::icon)
    ).apply(inst, GuideCategory::new));

    public static final Codec<GuideIndex> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CATEGORY_CODEC.listOf().fieldOf("categories").forGetter(GuideIndex::categories),
            THEME_CODEC.optionalFieldOf("theme", GuideTheme.FALLBACK).forGetter(GuideIndex::theme)
    ).apply(inst, GuideIndex::new));

    public Collection<String> knownCategoryIds() {
        return categories.stream().map(GuideCategory::id).toList();
    }

    public record GuideCategory(String id, String name, Icon icon) {
        public boolean isDefault() {
            return id.equals(DocMetadata.DEFAULT_CATEGORY);
        }
    }

    public record GuideTheme(Color4I bgColor, Color4I indexBgColor, Color4I guiColor, Color4I textColor, Color4I linkColor, Color4I codeColor) {
        public static final GuideTheme FALLBACK = new GuideTheme(
                Color4I.rgb(0x80000000),
                Color4I.rgb(0x212121),
                Color4I.rgb(0x606060),
                Color4I.rgb(0xFFFFFF),
                Color4I.rgb(0x98D9FF),
                Color4I.rgb(0xEBCB8B)
        );
    }
}
