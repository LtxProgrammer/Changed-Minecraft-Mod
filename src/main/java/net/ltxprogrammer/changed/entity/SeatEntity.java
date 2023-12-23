package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.block.SeatableBlock;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SeatEntity extends Entity {
    public static final EntityDataAccessor<Optional<BlockState>> BLOCK_STATE = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BLOCK_STATE);
    public static final EntityDataAccessor<BlockPos> BLOCK_POS = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Boolean> SEATED_INVISIBLE = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BOOLEAN);

    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public static SeatEntity createFor(Level level, BlockState state, BlockPos pos, boolean seatedInvisible) {
        if (level.isClientSide) {
            var listOfSeats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)); // Check for existing SeatEntities to prevent duplicates
            if (listOfSeats.isEmpty())
                return null;

            listOfSeats = listOfSeats.stream().filter(entity -> {
                return entity.getAttachedBlockPos().equals(pos) && entity.getAttachedBlockState().isPresent() && entity.getAttachedBlockState().get().getBlock() == state.getBlock();
            }).toList();

            if (listOfSeats.isEmpty())
                return null;

            return listOfSeats.get(0);
        }

        SeatEntity seat = ChangedEntities.SEAT_ENTITY.get().create(level);
        if (seat == null)
            return null;

        seat.entityData.set(BLOCK_STATE, Optional.of(state));
        seat.entityData.set(BLOCK_POS, pos);
        seat.entityData.set(SEATED_INVISIBLE, seatedInvisible);
        if (state.getBlock() instanceof SeatableBlock seatableBlock) {
            var offset = seatableBlock.getSitOffset(level, state, pos);
            seat.setPos(pos.getX() + 0.5 + offset.x, pos.getY() + 0.5, pos.getZ() + 0.5 + offset.z);
        }

        else
            seat.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        level.addFreshEntity(seat);
        return seat;
    }

    public Optional<BlockState> getAttachedBlockState() {
        return this.entityData.get(BLOCK_STATE);
    }

    public BlockPos getAttachedBlockPos() {
        return this.entityData.get(BLOCK_POS);
    }

    public boolean shouldSeatedBeInvisible() {
        return this.entityData.get(SEATED_INVISIBLE);
    }

    @Override
    public double getPassengersRidingOffset() {
        var blockState = getAttachedBlockState();

        if (blockState.isEmpty())
            return super.getPassengersRidingOffset();
        if (blockState.get().getBlock() instanceof SeatableBlock seatableBlock)
            return seatableBlock.getSitOffset(this.level, blockState.get(), getAttachedBlockPos()).y;
        return super.getPassengersRidingOffset();
    }

    @Override
    public boolean canCollideWith(@NotNull Entity entity) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) { return false; }

    @Override
    public void tick() {
        super.tick();

        var blockPos = getAttachedBlockPos();
        if (!level.isLoaded(blockPos))
            return; // Avoid loading chunks

        var oBlockState = getAttachedBlockState();
        if (oBlockState.isEmpty()) {
            this.discard();
            return;
        }

        var nBlockState = level.getBlockState(blockPos);
        if (nBlockState.getBlock() != oBlockState.get().getBlock()) {
            this.discard();
            return;
        }

        if (nBlockState != oBlockState.get()) {
            this.entityData.set(BLOCK_STATE, Optional.of(nBlockState));
            if (nBlockState.getBlock() instanceof SeatableBlock seatableBlock) {
                var offset = seatableBlock.getSitOffset(level, nBlockState, blockPos);
                this.setPos(blockPos.getX() + 0.5 + offset.x, blockPos.getY() - 0.5, blockPos.getZ() + 0.5 + offset.z);
            }

            else
                this.setPos(blockPos.getX() + 0.5, blockPos.getY() - 0.5, blockPos.getZ() + 0.5);
        }

        if (shouldSeatedBeInvisible() && this.getFirstPassenger() instanceof Player player && !player.isInvisible()) {
            player.setInvisible(true);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.shouldSeatedBeInvisible())
            this.getPassengers().forEach(entity -> entity.setInvisible(false));
        super.remove(reason);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Optional.empty());
        this.entityData.define(BLOCK_POS, BlockPos.ZERO);
        this.entityData.define(SEATED_INVISIBLE, false);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {

    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
