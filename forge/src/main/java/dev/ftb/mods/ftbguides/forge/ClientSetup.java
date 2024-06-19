package dev.ftb.mods.ftbguides.forge;

import dev.ftb.mods.ftbguides.client.GuideBookColor;
import dev.ftb.mods.ftbguides.registry.ModItems;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientSetup {
    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::itemColors);
    }

    private static void itemColors(RegisterColorHandlersEvent.Item event) {
        event.register(GuideBookColor.INSTANCE, ModItems.BOOK.get());
    }
}
