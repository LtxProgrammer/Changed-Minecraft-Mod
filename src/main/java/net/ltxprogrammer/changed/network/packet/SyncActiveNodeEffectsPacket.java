package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SyncActiveNodeEffectsPacket implements ChangedPacket {
    private final int id;
    private final List<NodeEffect> nodeEffects;

    public SyncActiveNodeEffectsPacket(int id, List<NodeEffect> nodeEffects) {
        this.id = id;
        this.nodeEffects = nodeEffects;
    }

    public SyncActiveNodeEffectsPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        this.nodeEffects = buffer.readCollection(ArrayList::new, collBuffer -> {
            var tag = collBuffer.readAnySizeNbt();
            return NodeEffect.EFFECT_CODEC.decode(NbtOps.INSTANCE, tag.get("data")).getOrThrow(false, onError -> {}).getFirst();
        });
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.id);
        buffer.writeCollection(this.nodeEffects, (collBuffer, value) -> {
            var data = NodeEffect.EFFECT_CODEC.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, onError -> {});
            CompoundTag tag = new CompoundTag();
            tag.put("data", data);
            collBuffer.writeNbt(tag);
        });
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var player = level.getEntity(this.id);

                ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(player), variant -> {
                    variant.setNodeEffects(this.nodeEffects);
                });
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
