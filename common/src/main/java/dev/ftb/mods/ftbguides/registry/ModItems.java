package dev.ftb.mods.ftbguides.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(FTBGuides.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FTBGuides.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(FTBGuides.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<Item> BOOK = ITEMS.register("book", GuideBookItem::new);
    public static final RegistrySupplier<DataComponentType<GuideBookData>> GUIDE_DATA = COMPONENTS.register("guidebook", () -> DataComponentType.<GuideBookData>builder()
            .persistent(GuideBookData.CODEC)
            .build());

    public static final RegistrySupplier<CreativeModeTab> ITEM_GROUP = CREATIVE_TAB.register("ftbguides", () -> CreativeTabRegistry.create(
            Component.translatable("itemGroup.ftbguides.ftbguides"), () -> new ItemStack(ModItems.BOOK.get())
    ));

    public static void register() {
        CREATIVE_TAB.register();
        COMPONENTS.register();
        ITEMS.register();
    }

}
