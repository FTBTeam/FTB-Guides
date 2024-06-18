package dev.ftb.mods.ftbguides;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ftb.mods.ftbguides.client.FTBGuidesClient;
import dev.ftb.mods.ftbguides.commands.OpenGuiCommand;
import dev.ftb.mods.ftbguides.commands.SetGuideCommand;
import dev.ftb.mods.ftbguides.docs.DocsLoader;
import dev.ftb.mods.ftbguides.net.FTBGuidesNet;
import dev.ftb.mods.ftbguides.registry.GuideBookData;
import dev.ftb.mods.ftbguides.registry.ModItems;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class FTBGuides {
    public static final String MOD_ID = "ftbguides";
    public static final String MOD_NAME = "FTB Guides";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static void init() {
        CommandRegistrationEvent.EVENT.register(FTBGuides::registerCommands);
        InteractionEvent.RIGHT_CLICK_ITEM.register(FTBGuides::rightClickItem);

        FTBGuidesNet.init();

        ModItems.register();

        EnvExecutor.runInEnv(Env.CLIENT, () -> FTBGuidesClient::init);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(FTBGuides.MOD_ID, path);
    }

    private static CompoundEventResult<ItemStack> rightClickItem(Player player, InteractionHand interactionHand) {
        if (player.level().isClientSide) {
            ItemStack stack = player.getItemInHand(interactionHand);
            GuideBookData data = stack.get(ModItems.GUIDE_DATA.get());
            if (data != null) {
                String target = data.guide();
                if (target.isEmpty()) {
                    return CompoundEventResult.pass();
                }

                FTBGuidesClient.openGui(target);
                return CompoundEventResult.interruptTrue(stack);
            }
        }
        return CompoundEventResult.pass();
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(MOD_ID)
                .then(OpenGuiCommand.register())
                .then(SetGuideCommand.register())
        );
    }

    public static ItemStack makeGuideBook(Item item, String page) {
        return makeGuideBook(new ItemStack(item), page);
    }

    public static ItemStack makeGuideBook(ItemStack stack, String page) {
        stack.set(ModItems.GUIDE_DATA.get(), new GuideBookData(page));
        return stack;
    }
}
