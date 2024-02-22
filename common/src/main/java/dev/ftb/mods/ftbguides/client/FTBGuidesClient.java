package dev.ftb.mods.ftbguides.client;

import dev.ftb.mods.ftbguides.client.gui.GuideScreen;
import net.minecraft.resources.ResourceLocation;

public class FTBGuidesClient {
    public static void openGui(ResourceLocation id) {
        // TODO set active page
        new GuideScreen().openGui();
    }
}
