package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.block.Pillow;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PillowBlockEntity extends BlockEntity {
    public LivingEntity entity;
    public SeatEntity entityHolder;
    private DyeColor color;

    public PillowBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.PILLOW.get(), pos, state);
        this.color = ((Pillow)this.getBlockState().getBlock()).getColor();
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

    public void setColor(DyeColor color) {
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PillowBlockEntity blockEntity) {
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
