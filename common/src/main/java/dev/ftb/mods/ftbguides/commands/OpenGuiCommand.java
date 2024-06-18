package dev.ftb.mods.ftbguides.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbguides.net.OpenGuiMessage;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class OpenGuiCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("open")
                .then(argument("id", StringArgumentType.greedyString())
                        .executes(ctx -> openGui(ctx.getSource(), StringArgumentType.getString(ctx, "id")))
                )
                .executes(ctx -> openGui(ctx.getSource(), null));
    }

    private static int openGui(CommandSourceStack source, @Nullable String id) throws CommandSyntaxException {
        NetworkManager.sendToPlayer(source.getPlayerOrException(), new OpenGuiMessage(id));
        return 1;
    }
}
