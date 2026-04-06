package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.entity.PathFinderMobDataExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AssimilatedEntitySyncPacket implements ChangedPacket {
    private final int entityId;
    private final boolean assimilated;

    public AssimilatedEntitySyncPacket(int entityId, boolean assimilated) {
        this.entityId = entityId;
        this.assimilated = assimilated;
    }

    public AssimilatedEntitySyncPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.assimilated = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeBoolean(assimilated);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (!(level.getEntity(entityId) instanceof PathFinderMobDataExtension ext))
                    throw new IllegalStateException("Entity is not a living entity");

                ext.markAsLatexAssimilated(assimilated);
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }
}
