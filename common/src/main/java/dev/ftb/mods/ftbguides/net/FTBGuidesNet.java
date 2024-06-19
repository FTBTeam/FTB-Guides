package dev.ftb.mods.ftbguides.net;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.ftb.mods.ftbguides.FTBGuides;

public interface FTBGuidesNet {
    SimpleNetworkManager NET = SimpleNetworkManager.create(FTBGuides.MOD_ID);

    MessageType OPEN_GUI = NET.registerS2C("open_gui", OpenGuiMessage::new);

    static void init() {
    }
}
