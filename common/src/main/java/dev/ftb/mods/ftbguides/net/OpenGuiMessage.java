package dev.ftb.mods.ftbguides.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftbguides.client.FTBGuidesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class OpenGuiMessage extends BaseS2CMessage {
    private final String id;

    public OpenGuiMessage(@Nullable String id) {
        this.id = id;
    }

    public OpenGuiMessage(FriendlyByteBuf buf) {
        id = buf.readNullable(FriendlyByteBuf::readUtf);
    }

    @Override
    public MessageType getType() {
        return FTBGuidesNet.OPEN_GUI;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNullable(id, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        FTBGuidesClient.openGui(id);
    }
}
