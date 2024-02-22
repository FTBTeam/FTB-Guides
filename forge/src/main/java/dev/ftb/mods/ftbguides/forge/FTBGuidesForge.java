package dev.ftb.mods.ftbguides.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.ftb.mods.ftbguides.FTBGuides;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FTBGuides.MOD_ID)
public class FTBGuidesForge {
    public FTBGuidesForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FTBGuides.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FTBGuides.init();
    }
}
