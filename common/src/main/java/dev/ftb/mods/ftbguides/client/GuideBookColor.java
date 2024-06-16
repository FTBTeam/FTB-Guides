package dev.ftb.mods.ftbguides.client;

import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.registry.GuideBookData;
import dev.ftb.mods.ftbguides.registry.ModItems;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import net.minecraft.Util;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public enum GuideBookColor implements ItemColor {
    INSTANCE;

    private static final Function<String,Integer> RIBBON_FUNC
            = Util.memoize(s -> Color4I.HSBtoRGB(1 - ((float) s.hashCode() / Integer.MAX_VALUE), 0.75f, 0.9f));
    private static final Function<String,Integer> BOOK_FUNC
            = Util.memoize(s -> Color4I.HSBtoRGB((float) s.hashCode() / Integer.MAX_VALUE, 0.4f, 0.8f));

    @Override
    public int getColor(ItemStack itemStack, int layer) {
        return switch (layer) {
            case 1 -> BOOK_FUNC.apply(getBookNamespace(itemStack));
            case 2 -> RIBBON_FUNC.apply(getBookNamespace(itemStack));
            default -> 0xFFFFFFFF;
        };
    }

    private static String getBookNamespace(ItemStack itemStack) {
        GuideBookData guideBookData = itemStack.get(ModItems.GUIDE_DATA.get());
        if (guideBookData == null || guideBookData.guide().isEmpty()) {
            return "";
        }

        return guideBookData.guide().split(":")[0];
    }
}
