package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.ltxprogrammer.changed.entity.TamableLatexEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber
public class CommandTame {
    private static final SimpleCommandExceptionType NO_OWNER = new SimpleCommandExceptionType(Component.translatable("command.changed.error.no_owner"));
    private static final SimpleCommandExceptionType NOT_TAMABLE = new SimpleCommandExceptionType(Component.translatable("command.changed.error.not_tamable"));
    private static final SimpleCommandExceptionType ALREADY_TAMED = new SimpleCommandExceptionType(Component.translatable("command.changed.error.already_tamed"));
    private static final SimpleCommandExceptionType ALREADY_UNTAMED = new SimpleCommandExceptionType(Component.translatable("command.changed.error.already_untamed"));

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tamechanged").requires(p -> p.hasPermission(2))
                .then(Commands.argument("entities", EntityArgument.entities())
                        .executes(context -> tameEntity(context.getSource(), EntityArgument.getEntities(context, "entities")))
                        .then(Commands.argument("owner", EntityArgument.player())
                                .executes(context -> tameEntityForPlayer(context.getSource(), EntityArgument.getEntities(context, "entities"), EntityArgument.getPlayer(context, "owner")))
                        )
                ));
        event.getDispatcher().register(Commands.literal("untamechanged").requires(p -> p.hasPermission(2))
                .then(Commands.argument("entities", EntityArgument.entities())
                        .executes(context -> untameEntity(context.getSource(), EntityArgument.getEntities(context, "entities")))
                ));
    }

    private static int tameEntity(CommandSourceStack source, Collection<? extends Entity> entities) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayer();
        if (player != null)
            return tameEntityForPlayer(source, entities, player);
        throw NO_OWNER.create();
    }

    private static int tameEntityForPlayer(CommandSourceStack source, Collection<? extends Entity> entities, ServerPlayer owner) throws CommandSyntaxException {
        int tameCount = 0;
        Entity lastTamed = null;
        for (Entity entity : entities) {
            if (entity instanceof TamableLatexEntity tamableLatex) {
                if (tamableLatex.getOwner() == owner) {
                    if (entities.size() == 1)
                        throw ALREADY_TAMED.create();
                    continue;
                }
                tamableLatex.tame(owner);
                lastTamed = entity;
                tameCount++;
            }
            else if (entities.size() == 1)
                throw NOT_TAMABLE.create();
        }
        if (tameCount > 1) {
            final int finalUntameCount = tameCount;
            source.sendSuccess(() -> Component.translatable("command.changed.success.tame.tamed.many",
                    finalUntameCount, owner.getScoreboardName()), false);
        } else if (tameCount == 1) {
            Entity finalLastTamed = lastTamed;
            source.sendSuccess(() -> Component.translatable("command.changed.success.tame.tamed.one",
                    finalLastTamed.getDisplayName(), owner.getScoreboardName()), false);
        }

        return tameCount;
    }

    private static int untameEntity(CommandSourceStack source, Collection<? extends Entity> entities) throws CommandSyntaxException {
        int untameCount = 0;
        Entity lastUntamed = null;
        for (Entity entity : entities) {
            if (entity instanceof TamableLatexEntity tamableLatex) {
                if (tamableLatex.getOwner() == null) {
                    if (entities.size() == 1)
                        throw ALREADY_UNTAMED.create();
                    continue;
                }
                tamableLatex.untame();
                lastUntamed = entity;
                untameCount++;
            }
            else if (entities.size() == 1)
                throw NOT_TAMABLE.create();
        }
        if (untameCount > 1) {
            final int finalUntameCount = untameCount;
            source.sendSuccess(() -> Component.translatable("command.changed.success.tame.untamed.many",
                    finalUntameCount), false);
        } else if (untameCount == 1) {
            Entity finalLastUntamed = lastUntamed;
            source.sendSuccess(() -> Component.translatable("command.changed.success.tame.untamed.one",
                    finalLastUntamed.getDisplayName()), false);
        }
        return untameCount;
    }
}
