package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
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
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class CommandTransfur {
    private static final SimpleCommandExceptionType NOT_LATEX_FORM = new SimpleCommandExceptionType(new TranslatableComponent("command.changed.error.not_latex_form"));
    private static final SimpleCommandExceptionType NO_SPECIAL_FORM = new SimpleCommandExceptionType(new TranslatableComponent("command.changed.error.no_special_form"));

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_LATEX_FORMS = SuggestionProviders.register(Changed.modResource("latex_forms"), (p_121667_, p_121668_) -> {
        return SharedSuggestionProvider.suggestResource(LatexVariant.PUBLIC_LATEX_FORMS.keySet(), p_121668_);
    });

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("transfur").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("form", ResourceLocationArgument.id()).suggests(SUGGEST_LATEX_FORMS)
                                .executes(context -> transfurPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "form")))
                        )));
        event.getDispatcher().register(Commands.literal("untransfur").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> untransfurPlayer(context.getSource(), EntityArgument.getPlayer(context, "player")))
                ));
        event.getDispatcher().register(Commands.literal("specialsyringe").requires(p -> p.hasPermission(3))
                .then(Commands.argument("uuid", UuidArgument.uuid())
                        .executes(context -> {
                            ItemStack stack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
                            Syringe.setUnpureVariant(stack, Changed.modResource("special/form_" + UuidArgument.getUuid(context, "uuid")));
                            context.getSource().getPlayerOrException().addItem(stack);
                            return Command.SINGLE_SUCCESS;
                        })
                ));
    }

    private static int transfurPlayer(CommandSourceStack source, ServerPlayer player, ResourceLocation form) throws CommandSyntaxException {
        if (LatexVariant.PUBLIC_LATEX_FORMS.containsKey(form)) {
            if (!form.equals(LatexVariant.SPECIAL_LATEX))
                ProcessTransfur.transfur(player, source.getLevel(), LatexVariant.PUBLIC_LATEX_FORMS.get(form), true);
            else {
                ResourceLocation key = Changed.modResource("special/form_" + player.getUUID());
                if (!LatexVariant.SPECIAL_LATEX_FORMS.containsKey(key))
                    throw NO_SPECIAL_FORM.create();

                ProcessTransfur.transfur(player, source.getLevel(), LatexVariant.SPECIAL_LATEX_FORMS.get(key), true);
            }
        }
        else
            throw NOT_LATEX_FORM.create();
        return Command.SINGLE_SUCCESS;
    }

    private static int untransfurPlayer(CommandSourceStack source, ServerPlayer player) {
        if (ProcessTransfur.isPlayerLatex(player)) {
            ProcessTransfur.getPlayerLatexVariant(player).unhookAll(player);
            ProcessTransfur.setPlayerLatexVariant(player, null);
            ProcessTransfur.setPlayerTransfurProgress(player, new ProcessTransfur.TransfurProgress(0, LatexVariant.LIGHT_LATEX_WOLF.male().getFormId()));
        }
        return Command.SINGLE_SUCCESS;
    }
}
