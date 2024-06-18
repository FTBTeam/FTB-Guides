package dev.ftb.mods.ftbguides.forge;

import dev.ftb.mods.ftbguides.FTBGuides;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(FTBGuides.MOD_ID)
public class FTBGuidesNeoforge {
    public FTBGuidesNeoforge(IEventBus modBuss) {
        if (FMLEnvironment.dist.isClient()) {
            ClientSetup.init(modBuss);
        }

        FTBGuides.init();
    }
}
