package net.ltxprogrammer.changed.block.entity;

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

import static net.ltxprogrammer.changed.block.CardboardBox.OPEN;
import static net.ltxprogrammer.changed.init.ChangedEntities.ENTITY_CONTAINER;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;

public class CardboardBoxBlockEntity extends BlockEntity {
    public static class EntityContainer extends Entity {
        public EntityContainer(EntityType<?> p_19870_, Level p_19871_) {
            super(p_19870_, p_19871_);
        }

        public void tick() {
            super.tick();

            if (!level.getBlockState(this.blockPosition()).is(ChangedBlocks.CARDBOARD_BOX.get()))
                this.remove(RemovalReason.DISCARDED);
            else if (this.getFirstPassenger() instanceof Player player && !player.isInvisible()) {
                player.setInvisible(true);
            }
        }

        public boolean canCollideWith(Entity p_38376_) {
            return false;
        }

        public boolean canBeCollidedWith() {
            return false;
        }

        public boolean isPushable() {
            return false;
        }

        public boolean hurt(DamageSource p_38319_, float p_38320_) { return false; }

        @Override
        protected void defineSynchedData() {}

        @Override
        protected void readAdditionalSaveData(CompoundTag p_20052_) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag p_20139_) {}

        @Override
        public Packet<?> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }
    }

    public LivingEntity entity;
    public EntityContainer entityHolder;
    public int ticksSinceChange = 20;
    public static final int OPEN_THRESHOLD = 15;

    public CardboardBoxBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ChangedBlockEntities.CARDBOARD_BOX.get(), p_155229_, p_155230_);
    }

    public boolean hideEntity(LivingEntity entity) {
        if (this.entity != null)
            return false;
        else if (entityHolder != null) {
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

    public static void tick(Level level, BlockPos pos, BlockState state, CardboardBoxBlockEntity blockEntity) {
        blockEntity.ticksSinceChange++;

        if (blockEntity.entityHolder == null) {
            blockEntity.entityHolder = ENTITY_CONTAINER.get().create(level);
            blockEntity.entityHolder.setPos(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5);
            level.addFreshEntity(blockEntity.entityHolder);
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
