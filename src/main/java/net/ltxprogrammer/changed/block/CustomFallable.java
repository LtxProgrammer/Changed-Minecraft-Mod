package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Fallable;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface CustomFallable extends Fallable {
    ResourceLocation getModelName();

    class UpdateFallingBlockEntityData implements ChangedPacket {
        private final int entityId;
        private final CompoundTag blockData;

        protected UpdateFallingBlockEntityData(FallingBlockEntity entity) {
            entityId = entity.getId();
            blockData = entity.blockData;
        }

        public UpdateFallingBlockEntityData(FriendlyByteBuf buffer) {
            entityId = buffer.readInt();
            blockData = buffer.readNbt();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeInt(entityId);
            buffer.writeNbt(blockData);
        }

        @Override
        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            var context = contextSupplier.get();

            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                if (UniversalDist.getLevel().getEntity(this.entityId) instanceof FallingBlockEntity blockEntity) {
                    blockEntity.blockData = this.blockData;
                    context.setPacketHandled(true);
                }
            }
        }
    }

    static UpdateFallingBlockEntityData updateBlockData(FallingBlockEntity entity) {
        return new UpdateFallingBlockEntityData(entity);
    }
}
