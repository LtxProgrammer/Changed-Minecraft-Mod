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

import static net.ltxprogrammer.changed.block.CardboardBoxTall.OPEN;

public class CardboardBoxTallBlockEntity extends BlockEntity implements SeatableBlockEntity {
    public SeatEntity entityHolder;
    public int ticksSinceChange = 20;
    public static final int OPEN_THRESHOLD = 15;

    public CardboardBoxTallBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ChangedBlockEntities.CARDBOARD_BOX_TALL.get(), p_155229_, p_155230_);
    }

    @Override
    public SeatEntity getEntityHolder() {
        return entityHolder;
    }

    @Override
    public void setEntityHolder(SeatEntity entityHolder) {
        this.entityHolder = entityHolder;
    }

    public boolean hideEntity(LivingEntity entity) {
        if (entityHolder == null || entityHolder.isRemoved()) {
            entityHolder = SeatEntity.createFor(entity.level, this.getBlockState(), this.getBlockPos(), true);
        }

        if (this.getSeatedEntity() != null)
            return false;
        else if (entityHolder != null) {
            if (!level.isClientSide)
                entity.startRiding(entityHolder);
            ticksSinceChange = 0;
            return true;
        }

        return false;
    }

    public void forceOutEntity() {
        final var entity = this.getSeatedEntity();
        if (entity != null && entity.vehicle == entityHolder) {
            entity.vehicle = null;
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CardboardBoxTallBlockEntity blockEntity) {
        blockEntity.ticksSinceChange++;

        final var entity = blockEntity.getSeatedEntity();

        if (entity != null && blockEntity.entityHolder != null) {
            if (entity.vehicle != blockEntity.entityHolder) {
                if (entity.vehicle == null || !entity.vehicle.blockPosition().equals(blockEntity.entityHolder.blockPosition())) {
                    blockEntity.ticksSinceChange = 0;
                }
            }
        }

        if (blockEntity.ticksSinceChange < OPEN_THRESHOLD) {
            if (!state.getValue(OPEN)) {
                BlockState belowState = level.getBlockState(pos.below());

                level.setBlock(pos, state.setValue(OPEN, true), 3);
                if (belowState.is(state.getBlock()))
                    level.setBlock(pos.below(), belowState.setValue(OPEN, true), 3);
            }
        }

        else {
            if (state.getValue(OPEN)) {
                BlockState belowState = level.getBlockState(pos.below());

                level.setBlock(pos, state.setValue(OPEN, false), 3);
                if (belowState.is(state.getBlock()))
                    level.setBlock(pos.below(), belowState.setValue(OPEN, false), 3);
            }
        }
    }
}
