package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class OfficeChairBlockEntity extends BlockEntity {
    public LivingEntity entity;
    public SeatEntity entityHolder;

    public OfficeChairBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.OFFICE_CHAIR.get(), pos, state);
    }

    public boolean sitEntity(LivingEntity entity) {
        if (this.entity != null)
            return false;
        else if (entityHolder != null) {
            this.entity = entity;
            this.entity.startRiding(entityHolder);
            return true;
        }

        return false;
    }

    public void forceOutEntity() {
        if (entity != null && entity.vehicle == entityHolder) {
            entity.vehicle = null;
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OfficeChairBlockEntity blockEntity) {
        if (blockEntity.entityHolder == null) {
            blockEntity.entityHolder = SeatEntity.createFor(level, state, pos, false);
        }

        if (blockEntity.entity != null) {
            if (blockEntity.entity.vehicle != blockEntity.entityHolder) {
                if (blockEntity.entity.vehicle == null || !blockEntity.entity.vehicle.blockPosition().equals(blockEntity.entityHolder.blockPosition())) {
                    blockEntity.entity = null;
                }
            }
        }
    }
}
