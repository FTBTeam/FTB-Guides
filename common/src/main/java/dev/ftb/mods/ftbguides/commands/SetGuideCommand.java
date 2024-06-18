package dev.ftb.mods.ftbguides.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbguides.registry.GuideBookData;
import dev.ftb.mods.ftbguides.registry.ModItems;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SetGuideCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("setguide")
                .requires(source -> source.hasPermission(2))
                .then(argument("id", StringArgumentType.greedyString())
                        .executes(ctx -> setGuide(ctx.getSource(), StringArgumentType.getString(ctx, "id")))
                );
    }

    private static int setGuide(CommandSourceStack source, String id) throws CommandSyntaxException {
        ItemStack stack = source.getPlayerOrException().getMainHandItem();
        if (!stack.isEmpty()) {
            stack.set(ModItems.GUIDE_DATA.get(), new GuideBookData(id));
            source.sendSuccess(() -> Component.translatable("ftbguides.message.set_data", stack.getDisplayName(), id), false);
            return 1;
        }
        return 0;
    }
}
