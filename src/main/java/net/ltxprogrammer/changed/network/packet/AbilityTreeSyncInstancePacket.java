package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.AbilityTrees;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.AccessoryAccessMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AbilityTreeSyncInstancePacket implements ChangedPacket {
    private final CompoundTag treeInfo;

    public AbilityTreeSyncInstancePacket(AbilityTreeInstance treeInstance) {
        this.treeInfo = treeInstance.save();
    }

    public AbilityTreeSyncInstancePacket(FriendlyByteBuf buffer) {
        this.treeInfo = buffer.readAnySizeNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(treeInfo);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                AbilityTreeInstance.getForPlayer(UniversalDist.getLocalPlayer())
                        .read(level, treeInfo);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
