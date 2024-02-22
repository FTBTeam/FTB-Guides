package dev.ftb.mods.ftbguides.fabric;

import dev.ftb.mods.ftbguides.FTBGuides;
import net.fabricmc.api.ModInitializer;

public class FTBGuidesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FTBGuides.init();
    }
}
