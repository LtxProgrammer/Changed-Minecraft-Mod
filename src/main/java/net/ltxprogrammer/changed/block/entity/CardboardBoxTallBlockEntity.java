package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.network.NetworkHooks;

import static net.ltxprogrammer.changed.block.CardboardBoxTall.OPEN;
import static net.ltxprogrammer.changed.init.ChangedEntities.SEAT_ENTITY;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;

public class CardboardBoxTallBlockEntity extends BlockEntity {
    public LivingEntity entity;
    public SeatEntity entityHolder;
    public int ticksSinceChange = 20;
    public static final int OPEN_THRESHOLD = 15;

    public CardboardBoxTallBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ChangedBlockEntities.CARDBOARD_BOX_TALL.get(), p_155229_, p_155230_);
    }

    public boolean hideEntity(LivingEntity entity) {
        if (this.entity != null)
            return false;
        else if (entityHolder != null) {
            if (!entityHolder.getPassengers().isEmpty())
                return false;
            this.entity = entity;
            this.entity.startRiding(entityHolder);
            ticksSinceChange = 0;
            return true;
        }

        return false;
    }

    public void forceOutEntity() {
        if (entity != null && entity.vehicle == entityHolder) {
            entity.vehicle = null;
            entity.setInvisible(false);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CardboardBoxTallBlockEntity blockEntity) {
        blockEntity.ticksSinceChange++;

        if (blockEntity.entityHolder == null) {
            blockEntity.entityHolder = SeatEntity.createFor(level, state, pos, true);
        }

        if (blockEntity.entity != null) {
            if (blockEntity.entity.vehicle != blockEntity.entityHolder) {
                if (blockEntity.entity.vehicle == null || !blockEntity.entity.vehicle.blockPosition().equals(blockEntity.entityHolder.blockPosition())) {
                    blockEntity.entity.setInvisible(false);
                    blockEntity.entity = null;
                    blockEntity.ticksSinceChange = 0;
                }
            }
        }

        if (blockEntity.ticksSinceChange < OPEN_THRESHOLD) {
            if (!state.getValue(OPEN)) {
                if (level.isClientSide)
                    level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ChangedSounds.BOW2, SoundSource.BLOCKS, 1.0f, 1.0f, true);
                level.setBlock(pos, state.setValue(OPEN, true), 3);
                level.setBlock(pos.below(), state.setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(OPEN, true), 3);
            }
        }

        else {
            if (state.getValue(OPEN)) {
                level.setBlock(pos, state.setValue(OPEN, false), 3);
                level.setBlock(pos.below(), state.setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false), 3);
            }
        }
    }
}
