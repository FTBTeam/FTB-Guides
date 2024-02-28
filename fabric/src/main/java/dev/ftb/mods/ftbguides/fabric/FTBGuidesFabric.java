package dev.ftb.mods.ftbguides.fabric;

import dev.ftb.mods.ftbguides.FTBGuides;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class FTBGuidesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FTBGuides.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(ClientSetup::clientStarted);
    }
}
