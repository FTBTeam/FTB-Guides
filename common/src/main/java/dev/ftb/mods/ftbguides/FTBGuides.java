package dev.ftb.mods.ftbguides;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.ftb.mods.ftbguides.commands.OpenGuiCommand;
import dev.ftb.mods.ftbguides.docs.DocsLoader;
import dev.ftb.mods.ftbguides.net.FTBGuidesNet;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTBGuides {
    public static final String MOD_ID = "ftbguides";
    public static final String MOD_NAME = "FTB Guides";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static void init() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new DocsLoader());

        CommandRegistrationEvent.EVENT.register(FTBGuides::registerCommands);

        FTBGuidesNet.init();
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(MOD_ID)
                .then(OpenGuiCommand.register())
        );
    }
}
