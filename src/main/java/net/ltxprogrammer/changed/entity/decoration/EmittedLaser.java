package net.ltxprogrammer.changed.entity.decoration;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.block.LaserEmitterBlock;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.BenignShorts;
import net.ltxprogrammer.changed.item.LaserReactiveItem;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.ItemUtil;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class EmittedLaser extends Entity {
    public static final EntityDataAccessor<BlockPos> DATA_EMITTER_POS_ID = SynchedEntityData.defineId(EmittedLaser.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Direction> DATA_DIRECTION_ID = SynchedEntityData.defineId(EmittedLaser.class, EntityDataSerializers.DIRECTION);
    public static final EntityDataAccessor<Float> DATA_LENGTH_ID = SynchedEntityData.defineId(EmittedLaser.class, EntityDataSerializers.FLOAT);

    public EmittedLaser(EntityType<? extends EmittedLaser> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static final int MAX_DISTANCE = 24;
    public static final int SUBSTEP_PRECISION = 16;

    @Override
    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    protected static float traceVoxelShape(VoxelShape shape, Direction travelDirection, float radius) {
        int stepX = travelDirection.getStepX();
        int stepY = travelDirection.getStepY();
        int stepZ = travelDirection.getStepZ();
        AABB checkAABB = new AABB(
                0.5 + 0.5 * stepX,
                0.5 + 0.5 * stepY,
                0.5 + 0.5 * stepZ,

                0.5 + -0.5 * stepX,
                0.5 + -0.5 * stepY,
                0.5 + -0.5 * stepZ
        ).inflate(
                (stepY + stepZ) * radius,
                (stepX + stepZ) * radius,
                (stepX + stepY) * radius
        );

        int traceSubstep = SUBSTEP_PRECISION;
        final float stepSize = 1f / SUBSTEP_PRECISION;

        while (traceSubstep > 0) {
            if (!Shapes.joinIsNotEmpty(shape, Shapes.create(checkAABB), BooleanOp.AND)) {
                break;
            }

            traceSubstep--;
            checkAABB = checkAABB.contract(
                    stepX * stepSize,
                    stepY * stepSize,
                    stepZ * stepSize
            );
        }

        if (traceSubstep == SUBSTEP_PRECISION)
            return 1f;
        return traceSubstep * stepSize;
    }

    protected float traceLevel(Level level, BlockPos emitterPos, Direction travelDirection, float radius) {
        int traceDistance = 0;
        float accum = 0f;

        BlockPos.MutableBlockPos mutableBlockPos = emitterPos.mutable();

        while (traceDistance < MAX_DISTANCE) {
            traceDistance++;

            mutableBlockPos.move(travelDirection);
            BlockState blockState = level.getBlockState(mutableBlockPos);
            if (!blockState.isAir() && !blockState.is(ChangedTags.Blocks.LASER_TRANSLUCENT)) {
                VoxelShape collision = blockState.getCollisionShape(level, mutableBlockPos, CollisionContext.of(this));
                float subStep = traceVoxelShape(collision, travelDirection, radius);
                if (subStep < 1f) {
                    accum += subStep;
                    break;
                }
            }
            FluidState fluidState = blockState.getFluidState();
            if (!fluidState.isEmpty() && !fluidState.is(ChangedTags.Fluids.LASER_TRANSLUCENT)) {
                VoxelShape collision = fluidState.getShape(level, mutableBlockPos);
                float subStep = traceVoxelShape(collision, travelDirection, radius);
                if (subStep < 1f) {
                    accum += subStep;
                    break;
                }
            }

            accum += 1f;
        }

        return accum;
    }

    public static Optional<EmittedLaser> create(Level level, BlockPos emitterPos, Direction travelDirection) {
        if (level.isClientSide)
            return Optional.empty();
        var beams = level.getEntities(EntityTypeTest.forClass(EmittedLaser.class), new AABB(emitterPos.relative(travelDirection)), beam -> {
            return beam.getEmitterPos().equals(emitterPos) && beam.getDirection() == travelDirection;
        });
        if (!beams.isEmpty())
            return Optional.empty();
        EmittedLaser laser = new EmittedLaser(ChangedEntities.EMITTED_LASER.get(), level);
        float length = laser.traceLevel(level, emitterPos, travelDirection, laser.getBeamRadius());
        if (length <= 0.0f)
            return Optional.empty();

        laser.entityData.set(DATA_EMITTER_POS_ID, emitterPos);
        laser.entityData.set(DATA_DIRECTION_ID, travelDirection);
        laser.entityData.set(DATA_LENGTH_ID, length);
        laser.setPos(laser.getBeamStart());

        return Optional.of(laser);
    }

    public void checkShapeAndApplyEffects() {
        if (this.level().isClientSide)
            return;

        BlockState emitterState = level().getBlockState(this.getEmitterPos());
        if (!emitterState.is(ChangedBlocks.LASER_EMITTER.get()) || !emitterState.getValue(LaserEmitterBlock.POWERED)) {
            this.discard();
            return;
        }

        float length = this.traceLevel(level(), this.getEmitterPos(), this.getDirection(), this.getBeamRadius());

        this.entityData.set(DATA_LENGTH_ID, length);
        this.reapplyPosition();

        if (length <= 0.0f) {
            return;
        }

        ObjectArrayList<LivingEntity> entities = new ObjectArrayList<>(0);
        level().getEntities(EntityTypeTest.forClass(LivingEntity.class), this.getBoundingBox(), Objects::nonNull, entities);
        final BlockPos emitterPos = this.entityData.get(DATA_EMITTER_POS_ID);

        entities.sort(Comparator.comparingDouble(leftEntity -> leftEntity.distanceToSqr(emitterPos.getX(), emitterPos.getY(), emitterPos.getZ())));

        if (!entities.isEmpty()) {
            final LivingEntity effectedEntity = entities.get(0);
            ItemUtil.getWearingItems(effectedEntity, itemStack -> itemStack.getItem() instanceof LaserReactiveItem).forEach(slottedItem -> {
                ((LaserReactiveItem)slottedItem.itemStack().getItem()).tickLaserExposure(effectedEntity, slottedItem.itemStack(), 11.0f);
            });
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(this.getBeamStart());

        if (tickCount % 5 == 0) {
            this.checkShapeAndApplyEffects();
        }
    }

    @Override
    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0f;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return true;
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        float length = this.getLength();
        float radius = this.getBeamRadius();
        BlockPos emittedPos = this.getEmitterPos();
        Direction direction = this.getDirection();

        Vec3 emitterStart = new Vec3(
                emittedPos.getX() + 0.5 + 0.5 * direction.getStepX(),
                emittedPos.getY() + 0.5 + 0.5 * direction.getStepY(),
                emittedPos.getZ() + 0.5 + 0.5 * direction.getStepZ()
        );

        Vec3 emitterEnd = emitterStart.add(
                direction.getStepX() * length,
                direction.getStepY() * length,
                direction.getStepZ() * length
        );

        return new AABB(emitterStart, emitterEnd).inflate(
                (direction.getStepY() + direction.getStepZ()) * radius,
                (direction.getStepX() + direction.getStepZ()) * radius,
                (direction.getStepX() + direction.getStepY()) * radius
        );
    }

    @Override
    protected void reapplyPosition() {
        this.setPos(this.getBeamStart());
    }

    public Vec3 getBeamStart() {
        BlockPos emittedPos = this.getEmitterPos();
        Direction direction = this.getDirection();
        return new Vec3(
                emittedPos.getX() + 0.5 + 0.5 * direction.getStepX(),
                emittedPos.getY() + 0.5 + 0.5 * direction.getStepY(),
                emittedPos.getZ() + 0.5 + 0.5 * direction.getStepZ()
        );
    }

    public Vec3 getBeamEnd() {
        float length = this.getLength();
        Direction direction = this.getDirection();
        return getBeamStart().add(
                direction.getStepX() * length,
                direction.getStepY() * length,
                direction.getStepZ() * length
        );
    }

    public float getBeamRadius() {
        return 0.125f;
    }

    public BlockPos getEmitterPos() {
        return this.entityData.get(DATA_EMITTER_POS_ID);
    }

    public @NotNull Direction getDirection() {
        return this.entityData.get(DATA_DIRECTION_ID);
    }

    public float getLength() {
        return this.entityData.get(DATA_LENGTH_ID);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_EMITTER_POS_ID, BlockPos.ZERO);
        this.entityData.define(DATA_DIRECTION_ID, Direction.NORTH);
        this.entityData.define(DATA_LENGTH_ID, 0f);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(DATA_DIRECTION_ID) || accessor.equals(DATA_LENGTH_ID) || accessor.equals(DATA_EMITTER_POS_ID)) {
            this.reapplyPosition();
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        if (tag.contains("emitterPos"))
            this.entityData.set(DATA_EMITTER_POS_ID, TagUtil.getBlockPos(tag, "emitterPos"));
        this.entityData.set(DATA_DIRECTION_ID, Objects.requireNonNullElse(Direction.byName(tag.getString("beamDirection")), Direction.NORTH));
        this.entityData.set(DATA_LENGTH_ID, tag.getFloat("beamLength"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        TagUtil.putBlockPos(tag, "emitterPos", this.entityData.get(DATA_EMITTER_POS_ID));
        tag.putString("beamDirection", this.entityData.get(DATA_DIRECTION_ID).getSerializedName());
        tag.putFloat("beamLength", this.entityData.get(DATA_LENGTH_ID));
    }
}
