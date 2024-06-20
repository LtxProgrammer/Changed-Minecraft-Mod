package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandUUID {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("uuid").requires(p -> p.hasPermission(1))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> fetchUuid(context.getSource(), EntityArgument.getPlayer(context, "player")))
                ));
    }

    private static int fetchUuid(CommandSourceStack source, ServerPlayer player) {
        source.sendSuccess(new TextComponent(player.getUUID().toString()), true);
        return Command.SINGLE_SUCCESS;
    }
}
