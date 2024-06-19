package dev.ftb.mods.ftbguides.fabric;

import dev.ftb.mods.ftbguides.client.GuideBookColor;
import dev.ftb.mods.ftbguides.registry.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;

public class ClientSetup {
    public static void clientStarted(Minecraft minecraft) {
        ColorProviderRegistry.ITEM.register(GuideBookColor.INSTANCE, ModItems.BOOK.get());
    }
}
