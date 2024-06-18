package dev.ftb.mods.ftbguides.net;

import dev.ftb.mods.ftblibrary.util.NetworkHelper;

public class FTBGuidesNet {
    public static void init() {
        NetworkHelper.registerS2C(OpenGuiMessage.TYPE, OpenGuiMessage.STREAM_CODEC, OpenGuiMessage::handle);
    }
}
