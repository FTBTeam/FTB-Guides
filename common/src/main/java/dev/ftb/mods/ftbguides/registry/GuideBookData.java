package dev.ftb.mods.ftbguides.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * This is done like this instead of just using a String so we can extend the data later without
 * having to break compatibility with existing data.
 */
public record GuideBookData(
        String guide
) {
    public static final Codec<GuideBookData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("guide").forGetter(GuideBookData::guide)
    ).apply(inst, GuideBookData::new));
}
