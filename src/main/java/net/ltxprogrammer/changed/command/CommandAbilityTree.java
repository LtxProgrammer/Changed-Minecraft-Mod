package net.ltxprogrammer.changed.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.AbilityTree;
import net.ltxprogrammer.changed.ability.tree.AbilityTrees;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandAbilityTree {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_TREES = SuggestionProviders.register(Changed.modResource("trees"), (p_121667_, p_121668_) -> {
        return SharedSuggestionProvider.suggestResource(AbilityTrees.INSTANCE.getTrees().stream().map(AbilityTree::getTreeLocation), p_121668_);
    });

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_NODES = SuggestionProviders.register(Changed.modResource("nodes"), (context, p_121668_) -> {
        var treeId = context.getArgument("tree", ResourceLocation.class);
        return SharedSuggestionProvider.suggestResource(AbilityTrees.INSTANCE.getTrees().stream().filter(abilityTree -> abilityTree.getTreeLocation().equals(treeId))
                .findFirst().map(AbilityTree::getTreeNodes).stream().flatMap(stream -> stream.map(Pair::getFirst)), p_121668_);
    });

    private static final SimpleCommandExceptionType NOT_TRANSFURRED = new SimpleCommandExceptionType(Component.translatable("command.changed.error.not_transfurred"));
    private static final SimpleCommandExceptionType NOT_TREE = new SimpleCommandExceptionType(Component.translatable("command.changed.error.not_tree"));
    private static final SimpleCommandExceptionType NOT_NODE = new SimpleCommandExceptionType(Component.translatable("command.changed.error.not_node"));

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("abilitytree").requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("tree", ResourceLocationArgument.id()).suggests(SUGGEST_TREES)
                                .then(Commands.literal("grant")
                                        .then(Commands.argument("node", ResourceLocationArgument.id()).suggests(SUGGEST_NODES)
                                                .executes(context -> grantTreeNode(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "tree"), ResourceLocationArgument.getId(context, "node")))
                                        )
                                )
                                .then(Commands.literal("grantall")
                                        .executes(context -> grantAllTreeNodes(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "tree")))
                                )
                                .then(Commands.literal("refund")
                                        .then(Commands.argument("node", ResourceLocationArgument.id()).suggests(SUGGEST_NODES)
                                                .executes(context -> refundTreeNode(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "tree"), ResourceLocationArgument.getId(context, "node")))
                                        )
                                )
                                .then(Commands.literal("refundall")
                                        .executes(context -> refundAllTreeNodes(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceLocationArgument.getId(context, "tree")))
                                )
                        )
                )
        );
    }

    private static int grantTreeNode(CommandSourceStack source, ServerPlayer player, ResourceLocation treeId, ResourceLocation nodeId) throws CommandSyntaxException {
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            throw NOT_TRANSFURRED.create();

        var abilityTree = ((PlayerDataExtension)player).getAbilityTree();
        var tree = abilityTree.getTrees(variant.getParent()).stream().filter(accountedTree -> accountedTree.getTree().getTreeLocation().equals(treeId)).findFirst();

        if (tree.isEmpty())
            throw NOT_TREE.create();

        var node = tree.get().getTree().getNamedNode(nodeId);
        if (node == null)
            throw NOT_NODE.create();

        int granted = tree.get().makePurchase(variant.getParent(), nodeId, 0) ? 1 : 0;

        if (granted > 0)
            source.sendSuccess(() -> Component.translatable("command.changed.success.abilitytree.grant",
                    node.getTitle(), player.getScoreboardName()), false);

        return granted;
    }

    private static int grantAllTreeNodes(CommandSourceStack source, ServerPlayer player, ResourceLocation treeId) throws CommandSyntaxException {
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            throw NOT_TRANSFURRED.create();

        var abilityTree = ((PlayerDataExtension)player).getAbilityTree();
        var tree = abilityTree.getTrees(variant.getParent()).stream().filter(accountedTree -> accountedTree.getTree().getTreeLocation().equals(treeId)).findFirst();

        if (tree.isEmpty())
            throw NOT_TREE.create();

        int granted = tree.get().getTree().getTreeNodes().map(Pair::getFirst).map(nodeId -> {
            return tree.get().makePurchase(variant.getParent(), nodeId, 0) ? 1 : 0;
        }).reduce(Integer::sum).orElse(0);

        if (granted > 0)
            source.sendSuccess(() -> Component.translatable("command.changed.success.abilitytree.grant.many", granted, player.getScoreboardName()), false);
        return granted;
    }

    private static int refundTreeNode(CommandSourceStack source, ServerPlayer player, ResourceLocation treeId, ResourceLocation nodeId) throws CommandSyntaxException {
        var abilityTree = ((PlayerDataExtension)player).getAbilityTree();
        var tree = abilityTree.getTrees().stream().filter(accountedTree -> accountedTree.getTree().getTreeLocation().equals(treeId)).findFirst();

        if (tree.isEmpty())
            throw NOT_TREE.create();

        if (!tree.get().getTree().hasNode(nodeId))
            throw NOT_NODE.create();

        int refunded = tree.get().refundNodePurchases(nodeId);

        if (refunded > 0)
            source.sendSuccess(() -> Component.translatable("command.changed.success.abilitytree.refund.many", refunded, player.getScoreboardName()), false);

        return refunded;
    }

    private static int refundAllTreeNodes(CommandSourceStack source, ServerPlayer player, ResourceLocation treeId) throws CommandSyntaxException {
        var abilityTree = ((PlayerDataExtension)player).getAbilityTree();
        var tree = abilityTree.getTrees().stream().filter(accountedTree -> accountedTree.getTree().getTreeLocation().equals(treeId)).findFirst();

        if (tree.isEmpty())
            throw NOT_TREE.create();

        int refunded = tree.get().getTree().getTreeNodes().map(Pair::getFirst).map(nodeId -> {
            return tree.get().refundNodePurchases(nodeId);
        }).reduce(Integer::sum).orElse(0);

        if (refunded > 0)
            source.sendSuccess(() -> Component.translatable("command.changed.success.abilitytree.refund.many", refunded, player.getScoreboardName()), false);
        return refunded;
    }
}
