package dev.ftb.mods.ftbguides.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FTBGuides.MOD_ID, Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<Item> BOOK = ITEMS.register("book", GuideBookItem::new);

    public static void register() {
        ITEMS.register();
    }
}
