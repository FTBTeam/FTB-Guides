package dev.ftb.mods.ftbguides.forge;

import dev.ftb.mods.ftbguides.client.GuideBookColor;
import dev.ftb.mods.ftbguides.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class ClientSetup {
    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::itemColors);
    }

    private static void itemColors(RegisterColorHandlersEvent.Item event) {
        event.register(GuideBookColor.INSTANCE, ModItems.BOOK.get());
    }
}
