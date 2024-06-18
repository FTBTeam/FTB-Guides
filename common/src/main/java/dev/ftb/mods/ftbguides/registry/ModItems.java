package dev.ftb.mods.ftbguides.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FTBGuides.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(FTBGuides.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<Item> BOOK = ITEMS.register("book", GuideBookItem::new);
    public static final RegistrySupplier<DataComponentType<GuideBookData>> GUIDE_DATA = COMPONENTS.register("guidebook", () -> DataComponentType.<GuideBookData>builder()
            .persistent(GuideBookData.CODEC)
            .build());

    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = RegistrarManager.get(FTBGuides.MOD_ID)
            .get(Registries.CREATIVE_MODE_TAB)
            .register(FTBGuides.rl("default"), () -> CreativeTabRegistry.create(builder -> {
                builder.title(Component.translatable("itemGroup.ftbguides.ftbguides"))
                    .icon(() -> new ItemStack(ModItems.BOOK.get()))
                    .displayItems((params, output) -> {
                        output.accept(new ItemStack(ModItems.BOOK.get()));
                    });
            }));

    public static void register() {
        COMPONENTS.register();
        ITEMS.register();
    }
}
