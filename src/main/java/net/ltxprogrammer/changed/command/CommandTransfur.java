package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.ImmediateTransfurDecision;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class CommandTransfur {
    private static final SimpleCommandExceptionType NOT_LATEX_FORM = new SimpleCommandExceptionType(Component.translatable("command.changed.error.not_latex_form"));
    private static final SimpleCommandExceptionType NOT_CAUSE = new SimpleCommandExceptionType(Component.translatable("command.changed.error.not_cause"));
    private static final SimpleCommandExceptionType USED_BY_OTHER_MOD = new SimpleCommandExceptionType(Component.translatable("command.changed.error.used_by_other_mod"));
    private static final SimpleCommandExceptionType NO_SPECIAL_FORM = new SimpleCommandExceptionType(Component.translatable("command.changed.error.no_special_form"));
    private static final ResourceLocation RANDOM_VARIANT = Changed.modResource("random");

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_TRANSFUR_VARIANT = SuggestionProviders.register(Changed.modResource("transfur_variant"), (p_121667_, p_121668_) -> {
        var list = TransfurVariant.getPublicTransfurVariants().map(ChangedRegistry.TRANSFUR_VARIANT::getKey).collect(Collectors.toCollection(ArrayList::new));
        list.add(TransfurVariant.SPECIAL_LATEX);
        list.add(RANDOM_VARIANT);
        return SharedSuggestionProvider.suggestResource(list, p_121668_);
    });

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_TRANSFUR_CAUSE = SuggestionProviders.register(Changed.modResource("cause"), (p_121667_, p_121668_) -> {
        var list = Arrays.stream(TransfurCause.values()).map(TransfurCause::getSerializedName);
        return SharedSuggestionProvider.suggest(list, p_121668_);
    });

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        var transfurNode = event.getDispatcher().register(Commands.literal("transfur").requires(p -> p.hasPermission(2))
                .then(Commands.argument("players", EntityArgument.player())
                        .then(Commands.argument("form", ResourceLocationArgument.id()).suggests(SUGGEST_TRANSFUR_VARIANT)
                                .executes(context -> transfurPlayers(context.getSource(),
                                        EntityArgument.getPlayers(context, "players"),
                                        ResourceLocationArgument.getId(context, "form"),
                                        TransfurCause.GRAB_REPLICATE.getSerializedName(),
                                        null))
                                .then(Commands.argument("cause", StringArgumentType.string()).suggests(SUGGEST_TRANSFUR_CAUSE)
                                        .executes(context -> transfurPlayers(context.getSource(),
                                                EntityArgument.getPlayers(context, "players"),
                                                ResourceLocationArgument.getId(context, "form"),
                                                StringArgumentType.getString(context, "cause"),
                                                null))
                                        .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                                .executes(context -> transfurPlayers(context.getSource(),
                                                        EntityArgument.getPlayers(context, "players"),
                                                        ResourceLocationArgument.getId(context, "form"),
                                                        StringArgumentType.getString(context, "cause"),
                                                        CompoundTagArgument.getCompoundTag(context, "nbt")))))
                        )));
        event.getDispatcher().register(Commands.literal("tf").requires(p -> p.hasPermission(2)).redirect(transfurNode));
        var progressTransfurNode = event.getDispatcher().register(Commands.literal("progresstransfur").requires(p -> p.hasPermission(2))
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("form", ResourceLocationArgument.id()).suggests(SUGGEST_TRANSFUR_VARIANT)
                                .then(Commands.argument("progression", FloatArgumentType.floatArg(0.0f))
                                        .executes(context -> progressPlayersTransfur(context.getSource(),
                                                EntityArgument.getPlayers(context, "players"),
                                                ResourceLocationArgument.getId(context, "form"),
                                                FloatArgumentType.getFloat(context, "progression"),
                                                TransfurCause.GRAB_REPLICATE.name()))
                                        .then(Commands.argument("cause", StringArgumentType.string()).suggests(SUGGEST_TRANSFUR_CAUSE).executes(context -> progressPlayersTransfur(context.getSource(),
                                                EntityArgument.getPlayers(context, "players"),
                                                ResourceLocationArgument.getId(context, "form"),
                                                FloatArgumentType.getFloat(context, "progression"),
                                                StringArgumentType.getString(context, "cause"))))
                                ))));
        event.getDispatcher().register(Commands.literal("progresstf").requires(p -> p.hasPermission(2)).redirect(progressTransfurNode));

        var regressTransfurNode = event.getDispatcher().register(Commands.literal("regresstransfur").requires(p -> p.hasPermission(2))
                .executes(context -> regressPlayersTransfur(context.getSource(),
                        Collections.singleton(context.getSource().getPlayerOrException()),
                        Float.MAX_VALUE))
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> regressPlayersTransfur(context.getSource(),
                                EntityArgument.getPlayers(context, "players"),
                                Float.MAX_VALUE))
                        .then(Commands.argument("regression", FloatArgumentType.floatArg(0.0f))
                                .executes(context -> regressPlayersTransfur(context.getSource(),
                                        EntityArgument.getPlayers(context, "players"),
                                        FloatArgumentType.getFloat(context, "regression")))
                        )));
        event.getDispatcher().register(Commands.literal("regresstf").requires(p -> p.hasPermission(2)).redirect(regressTransfurNode)
                .executes(context -> regressPlayersTransfur(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), Float.MAX_VALUE)));

        var untransfurNode = event.getDispatcher().register(Commands.literal("untransfur").requires(p -> p.hasPermission(2))
                .executes(context -> untransfurPlayers(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())))
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> untransfurPlayers(context.getSource(), EntityArgument.getPlayers(context, "players")))
                ));
        event.getDispatcher().register(Commands.literal("untf").requires(p -> p.hasPermission(2)).redirect(untransfurNode)
                .executes(context -> untransfurPlayers(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()))));
        if (SharedConstants.IS_RUNNING_IN_IDE || !FMLLoader.isProduction()) {
            event.getDispatcher().register(Commands.literal("specialsyringe").requires(p -> p.hasPermission(3))
                    .then(Commands.argument("uuid", UuidArgument.uuid())
                            .executes(context -> {
                                ItemStack stack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
                                Syringe.setUnpureVariant(stack, Changed.modResource("special/form_" + UuidArgument.getUuid(context, "uuid")));
                                if (!context.getSource().getPlayerOrException().addItem(stack))
                                    context.getSource().getPlayerOrException().drop(stack, false);
                                return Command.SINGLE_SUCCESS;
                            })
                    ));
        }

        event.getDispatcher().register(Commands.literal("gettf").requires(p -> p.hasPermission(1))
                .executes(context -> getTransfur(context.getSource(), context.getSource().getPlayerOrException()))
                .then(Commands.argument("players", EntityArgument.player())
                        .executes(context -> getTransfur(context.getSource(), EntityArgument.getPlayer(context, "players")))
                ));
    }

    private static int transfurPlayer(CommandSourceStack source, ServerPlayer player, ResourceLocation form, TransfurCause cause, @Nullable CompoundTag tag) throws CommandSyntaxException {
        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            throw USED_BY_OTHER_MOD.create();

        if (form.equals(RANDOM_VARIANT))
            form = Util.getRandom(TransfurVariant.getPublicTransfurVariants().collect(Collectors.toList()), player.getRandom()).getFormId();

        if (TransfurVariant.getPublicTransfurVariants().map(ChangedRegistry.TRANSFUR_VARIANT::getKey).anyMatch(form::equals)) {
            TransfurVariant<?> transfurVariant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form);
            ProcessTransfur.transfur(player, ImmediateTransfurDecision.safe(transfurVariant, cause, newEntity -> {
                if (tag != null)
                    newEntity.getChangedEntity().readPlayerVariantData(tag);
            }));
        }
        else if (form.equals(TransfurVariant.SPECIAL_LATEX)) {
            ResourceLocation key = Changed.modResource("special/form_" + player.getUUID());
            if (!ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(key))
                throw NO_SPECIAL_FORM.create();

            ProcessTransfur.transfur(player, ImmediateTransfurDecision.safe(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(key), cause));
        }
        else
            throw NOT_LATEX_FORM.create();
        return Command.SINGLE_SUCCESS;
    }

    private static int transfurPlayers(CommandSourceStack source, Collection<ServerPlayer> players, ResourceLocation form, String cause, @Nullable CompoundTag tag) throws CommandSyntaxException {
        final TransfurCause transfurCause = TransfurCause.fromSerial(cause).result().orElse(null);
        if (transfurCause == null)
            throw NOT_CAUSE.create();

        if (players.size() == 1) {
            final ServerPlayer player = players.stream().findFirst().get();
            int success = transfurPlayer(source, player, form, transfurCause, tag);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.transfurred.one", player.getScoreboardName(), form), false);
            return success;
        } else if (players.size() > 1) {
            int success = players.stream().map(player -> {
                try {
                    return transfurPlayer(source, player, form, transfurCause, tag);
                } catch (CommandSyntaxException e) {
                    return 0;
                }
            }).reduce(0, Integer::sum);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.transfurred.many", success, form), false);
            return success;
        } else
            return 0;
    }

    private static int progressPlayerTransfur(CommandSourceStack source, ServerPlayer player, ResourceLocation form, float progression, String cause) throws CommandSyntaxException {
        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            throw USED_BY_OTHER_MOD.create();

        final TransfurCause transfurCause;
        try {
            transfurCause = TransfurCause.valueOf(cause.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw NOT_CAUSE.create();
        }
        final TransfurContext context;
        if (source.getEntity() instanceof Player sourcePlayer)
            context = TransfurContext.playerLatexHazard(sourcePlayer, transfurCause);
        else if (source.getEntity() instanceof ChangedEntity sourceNpc)
            context = TransfurContext.npcLatexHazard(sourceNpc, transfurCause);
        else
            context = null;

        if (form.equals(RANDOM_VARIANT))
            form = Util.getRandom(TransfurVariant.getPublicTransfurVariants().collect(Collectors.toList()), player.getRandom()).getFormId();

        if (TransfurVariant.getPublicTransfurVariants().map(ChangedRegistry.TRANSFUR_VARIANT::getKey).anyMatch(form::equals)) {
            ProcessTransfur.progressTransfur(player, LatexAssimilationDecision.strongReplication(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form), context, progression));
        }
        else if (form.equals(TransfurVariant.SPECIAL_LATEX)) {
            ResourceLocation key = Changed.modResource("special/form_" + player.getUUID());
            if (!ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(key))
                throw NO_SPECIAL_FORM.create();

            ProcessTransfur.progressTransfur(player, LatexAssimilationDecision.strongReplication(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form), context, progression));
        }
        else
            throw NOT_LATEX_FORM.create();
        return Command.SINGLE_SUCCESS;
    }

    private static int progressPlayersTransfur(CommandSourceStack source, Collection<ServerPlayer> players, ResourceLocation form, float progression, String cause) throws CommandSyntaxException {
        if (players.size() == 1) {
            final ServerPlayer player = players.stream().findFirst().get();
            int success = progressPlayerTransfur(source, player, form, progression, cause);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.progresstf.one", player.getScoreboardName()), false);
            return success;
        } else if (players.size() > 1) {
            int success = players.stream().map(player -> {
                try {
                    return progressPlayerTransfur(source, player, form, progression, cause);
                } catch (CommandSyntaxException e) {
                    return 0;
                }
            }).reduce(0, Integer::sum);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.progresstf.many", success), false);
            return success;
        } else
            return 0;
    }

    private static int regressPlayerTransfur(CommandSourceStack source, ServerPlayer player, float regression) throws CommandSyntaxException {
        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            throw USED_BY_OTHER_MOD.create();

        float progress = ProcessTransfur.getPlayerTransfurProgress(player);
        if (progress > 0.0f)
            ProcessTransfur.setPlayerTransfurProgress(player, Math.max(progress - regression, 0.0f));

        return Command.SINGLE_SUCCESS;
    }

    private static int regressPlayersTransfur(CommandSourceStack source, Collection<ServerPlayer> players, float regression) throws CommandSyntaxException {
        if (players.size() == 1) {
            final ServerPlayer player = players.stream().findFirst().get();
            int success = regressPlayerTransfur(source, player, regression);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.regresstf.one", player.getScoreboardName()), false);
            return success;
        } else if (players.size() > 1) {
            int success = players.stream().map(player -> {
                try {
                    return regressPlayerTransfur(source, player, regression);
                } catch (CommandSyntaxException e) {
                    return 0;
                }
            }).reduce(0, Integer::sum);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.regresstf.many", success), false);
            return success;
        } else
            return 0;
    }

    private static int untransfurPlayer(CommandSourceStack source, ServerPlayer player) {
        return ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            variant.unhookAll(player);
            ProcessTransfur.removePlayerTransfurVariant(player);
            ProcessTransfur.setPlayerTransfurProgress(player, 0.0f);
        }) ? Command.SINGLE_SUCCESS : 0;
    }

    private static int untransfurPlayers(CommandSourceStack source, Collection<ServerPlayer> players) {
        if (players.size() == 1) {
            final ServerPlayer player = players.stream().findFirst().get();
            int success = untransfurPlayer(source, player);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.untransfurred.one", player.getScoreboardName()), false);
            return success;
        } else if (players.size() > 1) {
            int success = players.stream().map(player -> untransfurPlayer(source, player)).reduce(0, Integer::sum);
            if (success > 0)
                source.sendSuccess(() -> Component.translatable("command.changed.success.untransfurred.many", success), false);
            return success;
        } else
            return 0;
    }

    private static int getTransfur(CommandSourceStack source, ServerPlayer player, Predicate<TransfurVariantInstance<?>> test) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(player).filter(test).map(instance -> {
            source.sendSuccess(() -> Component.literal(player.getScoreboardName() + ": " + instance.getFormId().toString()), false);
            return 1;
        }).orElse(0);
    }

    private static int getTransfur(CommandSourceStack source, ServerPlayer player) {
        return getTransfur(source, player, variant -> true);
    }
}
