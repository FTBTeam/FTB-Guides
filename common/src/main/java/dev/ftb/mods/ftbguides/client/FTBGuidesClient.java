package dev.ftb.mods.ftbguides.client;

import dev.ftb.mods.ftbguides.client.gui.GuideScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FTBGuidesClient {
    public static void openGui(ResourceLocation id) {
        GuideScreen guideScreen = new GuideScreen();
        guideScreen.openGui();
        guideScreen.navigateTo(id.toString());
    }

    public static void displayError(Component error) {
        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, Component.translatable("ftbguides.gui.error"), error));
    }
}
