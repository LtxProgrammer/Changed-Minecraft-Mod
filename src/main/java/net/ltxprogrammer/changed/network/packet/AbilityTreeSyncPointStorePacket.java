package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.PartialNode;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AbilityTreeSyncPointStorePacket implements ChangedPacket {
    private final PartialNode.TreeReference tree;
    private final CompoundTag pointStoreInfo;
    private final boolean incomplete;

    public AbilityTreeSyncPointStorePacket(PartialNode.TreeReference tree, CompoundTag pointStoreInfo, boolean incomplete) {
        this.tree = tree;
        this.pointStoreInfo = pointStoreInfo;
        this.incomplete = incomplete;
    }

    public AbilityTreeSyncPointStorePacket(FriendlyByteBuf buffer) {
        this.tree = PartialNode.TreeReference.fromBuffer(buffer);
        this.pointStoreInfo = buffer.readAnySizeNbt();
        this.incomplete = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        tree.writeToBuffer(buffer);
        buffer.writeNbt(pointStoreInfo);
        buffer.writeBoolean(incomplete);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var localPlayer = UniversalDist.getLocalPlayer();
                AbilityTreeInstance.getForPlayer(localPlayer).getTree(tree)
                        .readPointStore(pointStoreInfo, incomplete);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
