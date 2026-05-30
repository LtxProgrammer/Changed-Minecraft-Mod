package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.beast.DoubleHeadedEntity;
import net.ltxprogrammer.changed.entity.beast.TripleHeadedEntity;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class MultiRotateHeadPacket implements ChangedPacket {
    private final int entityId;
    private final byte yHeadRot2;
    private final byte xHeadRot2;
    private final byte yHeadRot3;
    private final byte xHeadRot3;

    public MultiRotateHeadPacket(Entity entity, byte yHeadRot2, byte xHeadRot2, byte yHeadRot3, byte xHeadRot3) {
        this.entityId = entity.getId();
        this.yHeadRot2 = yHeadRot2;
        this.xHeadRot2 = xHeadRot2;
        this.yHeadRot3 = yHeadRot3;
        this.xHeadRot3 = xHeadRot3;
    }

    public MultiRotateHeadPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readVarInt();
        this.yHeadRot2 = buffer.readByte();
        this.xHeadRot2 = buffer.readByte();
        this.yHeadRot3 = buffer.readByte();
        this.xHeadRot3 = buffer.readByte();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
        buffer.writeByte(this.yHeadRot2);
        buffer.writeByte(this.xHeadRot2);
        buffer.writeByte(this.yHeadRot3);
        buffer.writeByte(this.xHeadRot3);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (!(this.getEntity(level) instanceof LivingEntity livingEntity))
                    return;

                var effectedEntity = EntityUtil.maybeGetOverlaying(livingEntity);

                if (effectedEntity instanceof DoubleHeadedEntity doubleHeadedEntity) {
                    float yRot = (float)(this.yHeadRot2 * 360) / 256.0F;
                    float xRot = (float)(this.xHeadRot2 * 360) / 256.0F;
                    doubleHeadedEntity.lerpHead2To(yRot, xRot, 3);
                }

                if (effectedEntity instanceof TripleHeadedEntity tripleHeadedEntity) {
                    float yRot = (float)(this.yHeadRot3 * 360) / 256.0F;
                    float xRot = (float)(this.xHeadRot3 * 360) / 256.0F;
                    tripleHeadedEntity.lerpRightHeadTo(yRot, xRot, 3);
                }
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
    }

    public Entity getEntity(Level level) {
        return level.getEntity(this.entityId);
    }
}