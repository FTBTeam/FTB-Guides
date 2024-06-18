package dev.ftb.mods.ftbguides.client;

import dev.architectury.registry.ReloadListenerRegistry;
import dev.ftb.mods.ftbguides.client.gui.GuideScreen;
import dev.ftb.mods.ftbguides.config.ClientConfig;
import dev.ftb.mods.ftbguides.docs.DocsLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

public class FTBGuidesClient {
    public static void init() {
        ClientConfig.init();

        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new DocsLoader());
    }

    public static void openGui(@Nullable String path) {
        GuideScreen guideScreen = new GuideScreen();
        guideScreen.openGui();
        if (path != null) {
            guideScreen.navigateTo(path);
        }
    }

    public static void displayError(Component error) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP, Component.translatable("ftbguides.gui.error"), error));
    }
}
