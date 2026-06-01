package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.AbilityTreeMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AbilityTreeMenuPacket implements ChangedPacket {
    public AbilityTreeMenuPacket(Opcode opcode, Optional<ResourceLocation> treeName, Optional<ResourceLocation> nodeName, Optional<Integer> parameter) {
        this.opcode = opcode;
        this.treeName = treeName;
        this.nodeName = nodeName;
        this.parameter = parameter;
    }

    public AbilityTreeMenuPacket(FriendlyByteBuf buffer) {
        this.opcode = Opcode.values()[buffer.readVarInt()];
        this.treeName = buffer.readOptional(FriendlyByteBuf::readResourceLocation);
        this.nodeName = buffer.readOptional(FriendlyByteBuf::readResourceLocation);
        this.parameter = buffer.readOptional(FriendlyByteBuf::readVarInt);
    }

    public enum Opcode {
        OPEN_MENU,
        MAKE_PURCHASE,
        AFFIRM_PURCHASE
    }

    private final Opcode opcode;
    private final Optional<ResourceLocation> treeName;
    private final Optional<ResourceLocation> nodeName;
    private final Optional<Integer> parameter;

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(opcode.ordinal());
        buffer.writeOptional(treeName, FriendlyByteBuf::writeResourceLocation);
        buffer.writeOptional(nodeName, FriendlyByteBuf::writeResourceLocation);
        buffer.writeOptional(parameter, FriendlyByteBuf::writeVarInt);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {

            });
        }

        else { // Mirror packet
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                var variantInstance = ProcessTransfur.getPlayerTransfurVariant(sender);
                if (variantInstance == null)
                    return CompletableFuture.failedFuture(new IllegalStateException("Sender is not transfurred"));

                var variant = variantInstance.getParent();
                switch (opcode) {
                    case OPEN_MENU -> {
                        sender.openMenu(new SimpleMenuProvider(
                                (id, inv, viewer) -> new AbilityTreeMenu(id, inv),
                                Component.empty()
                        ));
                    }
                    case MAKE_PURCHASE -> {
                        if (treeName.isEmpty() || nodeName.isEmpty())
                            return CompletableFuture.failedFuture(new IllegalArgumentException("TreeName and NodeName must be specified"));
                        var treeId = treeName.get();
                        var tree = AbilityTreeInstance.getForPlayer(sender).getTrees(variant).stream().filter(accountedTree -> accountedTree.getTree().getTreeLocation().equals(treeId)).findFirst();
                        if (tree.isEmpty())
                            return CompletableFuture.failedFuture(new IllegalArgumentException("Cannot find TreeName"));

                        return levelFuture.thenAccept(level -> {
                            if (!tree.get().hasPrerequisites(variant, nodeName.get()))
                                return;
                            if (!tree.get().canAfford(variant, nodeName.get()))
                                return;

                            int price = tree.get().getEffectivePrice(variant, nodeName.get());
                            if (tree.get().makePurchase(variant, nodeName.get(), price)) {
                                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender),
                                        new AbilityTreeMenuPacket(Opcode.AFFIRM_PURCHASE, treeName, nodeName, Optional.of(price)));
                            } else {
                                return;
                            }
                        });
                    }
                    case AFFIRM_PURCHASE -> {
                        if (treeName.isEmpty() || nodeName.isEmpty())
                            return CompletableFuture.failedFuture(new IllegalArgumentException("TreeName and NodeName must be specified"));
                        var treeId = treeName.get();
                        var tree = AbilityTreeInstance.getForPlayer(sender).getTrees(variant).stream().filter(accountedTree -> accountedTree.getTree().getTreeLocation().equals(treeId)).findFirst();
                        if (tree.isEmpty())
                            return CompletableFuture.failedFuture(new IllegalArgumentException("Cannot find TreeName"));
                        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender),
                                new AbilityTreeMenuPacket(Opcode.AFFIRM_PURCHASE, treeName, nodeName,
                                        tree.get().getNodeState(variant, nodeName.get()).map(state -> 1)));
                    }
                }
            }
            context.setPacketHandled(true);
            return CompletableFuture.completedFuture(null);
        }
    }
}
