package dev.ftb.mods.ftbguides.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbguides.net.OpenGuiMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class OpenGuiCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("open")
                .then(argument("id", ResourceLocationArgument.id())
                        .executes(ctx -> openGui(ctx.getSource(), ResourceLocationArgument.getId(ctx, "id")))
                );
    }

    private static int openGui(CommandSourceStack source, ResourceLocation id) throws CommandSyntaxException {
        new OpenGuiMessage(id).sendTo(source.getPlayerOrException());
        return 1;
    }
}
