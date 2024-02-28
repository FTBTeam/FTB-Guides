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
import dev.ftb.mods.ftbguides.docs.DocsLoader;
import dev.ftb.mods.ftbguides.net.FTBGuidesNet;
import dev.ftb.mods.ftbguides.registry.ModItems;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTBGuides {
    public static final String MOD_ID = "ftbguides";
    public static final String MOD_NAME = "FTB Guides";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final CreativeModeTab ITEM_GROUP = CreativeTabRegistry.create(
            new ResourceLocation(MOD_ID, MOD_ID), () -> new ItemStack(ModItems.BOOK.get())
    );

    public static void init() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new DocsLoader());

        CommandRegistrationEvent.EVENT.register(FTBGuides::registerCommands);
        InteractionEvent.RIGHT_CLICK_ITEM.register(FTBGuides::rightClickItem);

        FTBGuidesNet.init();

        ModItems.register();

        EnvExecutor.runInEnv(Env.CLIENT, () -> FTBGuidesClient::init);
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(FTBGuides.MOD_ID, path);
    }

    private static CompoundEventResult<ItemStack> rightClickItem(Player player, InteractionHand interactionHand) {
        if (player.level.isClientSide) {
            ItemStack stack = player.getItemInHand(interactionHand);
            if (stack.hasTag()) {
                String target = stack.getTag().getString(MOD_ID + ":page");
                if (!target.isEmpty()) {
                    FTBGuidesClient.openGui(target);
                    return CompoundEventResult.interruptTrue(stack);
                }
            }
        }
        return CompoundEventResult.pass();
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(MOD_ID)
                .then(OpenGuiCommand.register())
        );
    }

    public static ItemStack makeGuideBook(Item item, String page) {
        return makeGuideBook(new ItemStack(item), page);
    }

    public static ItemStack makeGuideBook(ItemStack stack, String page) {
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        tag.putString(MOD_ID + ":page", page);
        stack.setTag(tag);
        return stack;
    }
}
