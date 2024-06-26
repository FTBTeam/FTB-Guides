package dev.ftb.mods.ftbguides.client;

import dev.ftb.mods.ftbguides.client.gui.GuideScreen;
import dev.ftb.mods.ftbguides.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class FTBGuidesClient {
    public static void init() {
        ClientConfig.init();
    }

    public static void openGui(@Nullable String path) {
        GuideScreen guideScreen = new GuideScreen();
        guideScreen.openGui();
        if (path != null) {
            guideScreen.navigateTo(path);
        }
    }

    public static void displayError(Component error) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, Component.translatable("ftbguides.gui.error"), error));
    }
}
