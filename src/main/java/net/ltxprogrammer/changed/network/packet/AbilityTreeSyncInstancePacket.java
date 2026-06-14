package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.ability.tree.AbilityTree;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AbilityTreeSyncInstancePacket implements ChangedPacket {
    private final CompoundTag treeInfo;
    private final boolean incomplete;

    protected AbilityTreeSyncInstancePacket(CompoundTag tag, boolean incomplete) {
        this.treeInfo = tag;
        this.incomplete = incomplete;
    }

    public static AbilityTreeSyncInstancePacket ofAllTrees(AbilityTreeInstance treeInstance) {
        return new AbilityTreeSyncInstancePacket(treeInstance.save(), false);
    }

    public static AbilityTreeSyncInstancePacket ofActiveTrees(AbilityTreeInstance treeInstance, TransfurVariant<?> variant) {
        return new AbilityTreeSyncInstancePacket(treeInstance.saveActive(variant), true);
    }

    public static AbilityTreeSyncInstancePacket ofTree(AbilityTreeInstance treeInstance, AbilityTree tree) {
        return new AbilityTreeSyncInstancePacket(treeInstance.saveTree(tree), true);
    }

    public AbilityTreeSyncInstancePacket(FriendlyByteBuf buffer) {
        this.treeInfo = buffer.readAnySizeNbt();
        this.incomplete = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(treeInfo);
        buffer.writeBoolean(incomplete);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var localPlayer = UniversalDist.getLocalPlayer();
                AbilityTreeInstance.getForPlayer(localPlayer)
                        .read(localPlayer, treeInfo, incomplete);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
