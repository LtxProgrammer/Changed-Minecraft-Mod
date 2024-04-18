package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.ArrayList;
import java.util.Arrays;

@Mod.EventBusSubscriber
public class CommandTransfur {
    private static final SimpleCommandExceptionType NOT_LATEX_FORM = new SimpleCommandExceptionType(new TranslatableComponent("command.changed.error.not_latex_form"));
    private static final SimpleCommandExceptionType NOT_CAUSE = new SimpleCommandExceptionType(new TranslatableComponent("command.changed.error.not_cause"));
    private static final SimpleCommandExceptionType USED_BY_OTHER_MOD = new SimpleCommandExceptionType(new TranslatableComponent("command.changed.error.used_by_other_mod"));
    private static final SimpleCommandExceptionType NO_SPECIAL_FORM = new SimpleCommandExceptionType(new TranslatableComponent("command.changed.error.no_special_form"));

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_LATEX_FORMS = SuggestionProviders.register(Changed.modResource("latex_forms"), (p_121667_, p_121668_) -> {
        var list = new ArrayList<>(TransfurVariant.PUBLIC_LATEX_FORMS);
        list.add(TransfurVariant.SPECIAL_LATEX);
        return SharedSuggestionProvider.suggestResource(list, p_121668_);
    });

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_TRANSFUR_CAUSE = SuggestionProviders.register(Changed.modResource("cause"), (p_121667_, p_121668_) -> {
        var list = Arrays.stream(TransfurCause.values()).map(TransfurCause::name).map(String::toLowerCase);
        return SharedSuggestionProvider.suggest(list, p_121668_);
    });

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        var transfurNode = event.getDispatcher().register(Commands.literal("transfur").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("form", ResourceLocationArgument.id()).suggests(SUGGEST_LATEX_FORMS)
                                .executes(context -> transfurPlayer(context.getSource(),
                                        EntityArgument.getPlayer(context, "player"),
                                        ResourceLocationArgument.getId(context, "form"),
                                        TransfurCause.GRAB_REPLICATE.name()))
                                .then(Commands.argument("cause", StringArgumentType.string()).suggests(SUGGEST_TRANSFUR_CAUSE)
                                        .executes(context -> transfurPlayer(context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                ResourceLocationArgument.getId(context, "form"),
                                                StringArgumentType.getString(context, "cause"))))
                        )));
        event.getDispatcher().register(Commands.literal("tf").requires(p -> p.hasPermission(2)).redirect(transfurNode));
        event.getDispatcher().register(Commands.literal("progresstransfur").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("form", ResourceLocationArgument.id()).suggests(SUGGEST_LATEX_FORMS)
                                .then(Commands.argument("progression", FloatArgumentType.floatArg(0.0f))
                                        .executes(context -> progressPlayerTransfur(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "form"), FloatArgumentType.getFloat(context, "progression")))
                                ))));
        var untransfurNode = event.getDispatcher().register(Commands.literal("untransfur").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> untransfurPlayer(context.getSource(), EntityArgument.getPlayer(context, "player")))
                ));
        event.getDispatcher().register(Commands.literal("untf").requires(p -> p.hasPermission(2)).redirect(untransfurNode));
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
    }

    private static int transfurPlayer(CommandSourceStack source, ServerPlayer player, ResourceLocation form, String cause) throws CommandSyntaxException {
        final TransfurCause transfurCause;
        try {
            transfurCause = TransfurCause.valueOf(cause.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw NOT_CAUSE.create();
        }

        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            throw USED_BY_OTHER_MOD.create();

        if (TransfurVariant.PUBLIC_LATEX_FORMS.contains(form)) {
            ProcessTransfur.transfur(player, source.getLevel(), ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form), true,
                    TransfurContext.hazard(transfurCause));
        }
        else if (form.equals(TransfurVariant.SPECIAL_LATEX)) {
            ResourceLocation key = Changed.modResource("special/form_" + player.getUUID());
            if (!TransfurVariant.SPECIAL_LATEX_FORMS.contains(key))
                throw NO_SPECIAL_FORM.create();

            ProcessTransfur.transfur(player, source.getLevel(), ChangedRegistry.TRANSFUR_VARIANT.get().getValue(key), true,
                    TransfurContext.hazard(transfurCause));
        }
        else
            throw NOT_LATEX_FORM.create();
        return Command.SINGLE_SUCCESS;
    }

    private static int progressPlayerTransfur(CommandSourceStack source, ServerPlayer player, ResourceLocation form, float progression) throws CommandSyntaxException {
        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            throw USED_BY_OTHER_MOD.create();

        if (TransfurVariant.PUBLIC_LATEX_FORMS.contains(form)) {
            ProcessTransfur.progressTransfur(player, progression, ChangedRegistry.TRANSFUR_VARIANT.get().getValue(form));
        }
        else if (form.equals(TransfurVariant.SPECIAL_LATEX)) {
            ResourceLocation key = Changed.modResource("special/form_" + player.getUUID());
            if (!TransfurVariant.SPECIAL_LATEX_FORMS.contains(key))
                throw NO_SPECIAL_FORM.create();

            ProcessTransfur.progressTransfur(player, progression, ChangedRegistry.TRANSFUR_VARIANT.get().getValue(key));
        }
        else
            throw NOT_LATEX_FORM.create();
        return Command.SINGLE_SUCCESS;
    }

    private static int untransfurPlayer(CommandSourceStack source, ServerPlayer player) {
        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            variant.unhookAll(player);
            ProcessTransfur.removePlayerTransfurVariant(player);
            ProcessTransfur.setPlayerTransfurProgress(player, 0.0f);
        });
        return Command.SINGLE_SUCCESS;
    }
}
