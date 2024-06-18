package dev.ftb.mods.ftbguides.net;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.client.FTBGuidesClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenGuiMessage(String id) implements CustomPacketPayload {
    public static final Type<OpenGuiMessage> TYPE = new Type<>(FTBGuides.rl("open_gui"));

    public static final StreamCodec<FriendlyByteBuf, OpenGuiMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenGuiMessage::id,
            OpenGuiMessage::new
    );

    public static void handle(OpenGuiMessage message, NetworkManager.PacketContext context) {
        context.queue(() -> FTBGuidesClient.openGui(message.id));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
