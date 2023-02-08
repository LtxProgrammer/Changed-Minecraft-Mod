package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.ltxprogrammer.changed.process.Pale;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandPale {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("set_pale_exposure").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("form", IntegerArgumentType.integer(0, 72000))
                                .executes(context -> {
                                    Pale.setPaleExposure(EntityArgument.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "form"));
                                    return Command.SINGLE_SUCCESS;
                                })
                        )));
        event.getDispatcher().register(Commands.literal("cure_pale").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            Pale.tryCure(EntityArgument.getPlayer(context, "player"));
                            return Command.SINGLE_SUCCESS;
                        })
                ));
    }
}
