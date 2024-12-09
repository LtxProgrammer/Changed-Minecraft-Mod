package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.entity.animation.AnimationEvent;
import net.ltxprogrammer.changed.init.ChangedAnimationEvents;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessEmote;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber
public class CommandAnimate {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ANIMATIONS = SuggestionProviders.register(Changed.modResource("animations"), (p_121667_, p_121668_) -> {
        return SharedSuggestionProvider.suggestResource(ChangedRegistry.ANIMATION_EVENTS.get().getKeys().stream(), p_121668_);
    });

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("animate").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("animate", ResourceLocationArgument.id()).suggests(SUGGEST_ANIMATIONS)
                                .executes(context -> doAnimate(context.getSource(), EntityArgument.getPlayer(context, "player"), ChangedRegistry.ANIMATION_EVENTS.get().getValue(ResourceLocationArgument.getId(context, "animate"))))
                ))
        );
    }

    private static int doAnimate(CommandSourceStack source, ServerPlayer player, AnimationEvent<?> event) throws CommandSyntaxException {
        ChangedAnimationEvents.broadcastEntityAnimation(player, event, null);
        return Command.SINGLE_SUCCESS;
    }
}
