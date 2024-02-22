package dev.ftb.mods.ftbguides;

import dev.architectury.registry.ReloadListenerRegistry;
import dev.ftb.mods.docs.DocsLoader;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTBGuides {
    public static final String MOD_ID = "ftbguides";
    public static final String MOD_NAME = "FTB Guides";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static void init() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new DocsLoader());
    }
}
