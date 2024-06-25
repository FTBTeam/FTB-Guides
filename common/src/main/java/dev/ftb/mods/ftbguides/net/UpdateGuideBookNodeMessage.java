package dev.ftb.mods.ftbguides.net;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbguides.FTBGuides;
import dev.ftb.mods.ftbguides.registry.GuideBookData;
import dev.ftb.mods.ftbguides.registry.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record UpdateGuideBookNodeMessage(String newPageId) implements CustomPacketPayload {
    public static final Type<UpdateGuideBookNodeMessage> TYPE = new Type<>(FTBGuides.rl("update_guide_book_node"));

    public static final StreamCodec<FriendlyByteBuf, UpdateGuideBookNodeMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UpdateGuideBookNodeMessage::newPageId,
            UpdateGuideBookNodeMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateGuideBookNodeMessage message, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayer player) {
                GuideBookData data = player.getMainHandItem().get(ModItems.GUIDE_DATA.get());
                if (data != null) {
                    player.getMainHandItem().set(ModItems.GUIDE_DATA.get(), new GuideBookData(message.newPageId));
                }
            }
        });
    }
}
