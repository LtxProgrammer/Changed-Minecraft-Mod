package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.SeatableBlock;
import net.ltxprogrammer.changed.block.entity.SeatableBlockEntity;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.network.packet.SeatEntityInfoPacket;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        final BlockPos attached = this.getAttachedBlockPos();
        if (level.isLoaded(attached) && level.getBlockEntity(attached) instanceof SeatableBlockEntity seatableBlockEntity) {
            var existing = seatableBlockEntity.getEntityHolder();
            if (existing == null || existing.isRemoved())
                seatableBlockEntity.setEntityHolder(this);
        }
    }

    private static SeatEntity actuallyCreateFor(Level level, BlockState state, BlockPos pos, boolean seatedInvisible) {
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

        return seat;
    }

    public static SeatEntity createFor(Level level, BlockState state, BlockPos pos, boolean seatedInvisible) {
        if (level.isClientSide) {
            var seatEntity = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos)).stream().filter(entity -> {
                return entity.getAttachedBlockPos().equals(pos) && entity.getAttachedBlockState().isPresent() && entity.getAttachedBlockState().get().getBlock() == state.getBlock();
            }).findFirst(); // Check for existing SeatEntities to prevent duplicates

            return seatEntity.orElseGet(() -> {
                Changed.PACKET_HANDLER.sendToServer(new SeatEntityInfoPacket(pos)); // Request the server send information for the seat entity
                return actuallyCreateFor(level, state, pos, seatedInvisible);
            });
        }

        SeatEntity seat = actuallyCreateFor(level, state, pos, seatedInvisible);
        if (seat != null)
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

        if (this.getFirstPassenger() != null)
            this.tickCount = 0;
        else if (this.tickCount > 100)
            this.discard();
    }

    @Override
    protected void addPassenger(@NotNull Entity entity) {
        super.addPassenger(entity);

        this.getAttachedBlockState().ifPresent(blockState -> {
            if (blockState.getBlock() instanceof SeatableBlock seatableBlock) {
                seatableBlock.onEnterSeat(entity.level, blockState, this.getAttachedBlockPos(), entity);
            }
        });
    }

    @Override
    protected void removePassenger(@NotNull Entity entity) {
        super.removePassenger(entity);

        this.getAttachedBlockState().ifPresent(blockState -> {
            if (blockState.getBlock() instanceof SeatableBlock seatableBlock) {
                seatableBlock.onExitSeat(entity.level, blockState, this.getAttachedBlockPos(), entity);
            }
        });
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Optional.empty());
        this.entityData.define(BLOCK_POS, BlockPos.ZERO);
        this.entityData.define(SEATED_INVISIBLE, false);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        if (tag.contains("attachedBlockState"))
            this.entityData.set(BLOCK_STATE, Optional.of(NbtUtils.readBlockState(tag.getCompound("attachedBlockState"))));
        if (tag.contains("attachedBlockPos"))
            this.entityData.set(BLOCK_POS, TagUtil.getBlockPos(tag, "attachedBlockPos"));
        if (tag.contains("seatedInvisible"))
            this.entityData.set(SEATED_INVISIBLE, tag.getBoolean("seatedInvisible"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        this.getAttachedBlockState().ifPresent(state -> {
            tag.put("attachedBlockState", NbtUtils.writeBlockState(state));
        });
        TagUtil.putBlockPos(tag, "attachedBlockPos", this.getAttachedBlockPos());
        tag.putBoolean("seatedInvisible", this.shouldSeatedBeInvisible());
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
