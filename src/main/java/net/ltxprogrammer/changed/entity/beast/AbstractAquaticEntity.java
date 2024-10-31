package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public abstract class AbstractAquaticEntity extends ChangedEntity implements AquaticEntity {
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    private static boolean isDeepEnoughToSpawn(LevelAccessor p_32367_, BlockPos p_32368_) {
        return p_32368_.getY() < p_32367_.getSeaLevel() - 5;
    }

    public static <T extends ChangedEntity> boolean checkEntitySpawnRules(EntityType<T> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random random) {
        if (!world.canSeeSkyFromBelowWater(pos))
            return false;
        if (random.nextFloat() > 0.5f)
            return false;

        if (!world.getFluidState(pos.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            Holder<Biome> holder = world.getBiome(pos);
            boolean flag = world.getDifficulty() != Difficulty.PEACEFUL && (reason == MobSpawnType.SPAWNER || world.getFluidState(pos).is(FluidTags.WATER));
            if (!holder.is(Biomes.RIVER) && !holder.is(Biomes.FROZEN_RIVER)) {
                return random.nextInt(40) == 0 && isDeepEnoughToSpawn(world, pos) && flag;
            } else {
                return random.nextInt(15) == 0 && flag;
            }
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    public AbstractAquaticEntity(EntityType<? extends AbstractAquaticEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.moveControl = new AbstractAquaticEntity.AquaticMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, p_19871_);
        this.groundNavigation = new GroundPathNavigation(this, p_19871_);
        this.groundNavigation.setCanOpenDoors(true);
    }

    @Override
    public int getDepthStriderLevel() {
        return 2;
    }

    @Override
    public int getTicksRequiredToFreeze() { return 100; }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.REPLICATION; }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean canBreatheUnderwater() { return true; }

    @Override
    public void readAdditionalSaveData(CompoundTag p_20052_) {
        super.readAdditionalSaveData(p_20052_);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return super.getArmorSlots();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return super.getItemBySlot(p_21127_);
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot p_21036_, @NotNull ItemStack p_21037_) {
        super.setItemSlot(p_21036_, p_21037_);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag p_20139_) {
        super.addAdditionalSaveData(p_20139_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 0.3));
        this.goalSelector.addGoal(1, new SinkFromSurfaceGoal(this, 0.3));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 0.4D, 10));
    }

    @Override
    public Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? Color3.GRAY : Color3.WHITE;
    }

    public void travel(@NotNull Vec3 p_32394_) {
        boolean animateSwim = this.isInWater()
                && this.canFitInWater(this.position());

        if (this.isEffectiveAi() && animateSwim) {
            this.moveRelative(0.01F, p_32394_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(p_32394_);
        }

    }

    protected float getWaterDepth(BlockPos pos) {
        float depth = 0.0f;

        for (int i = 0; i < 3; ++i) {
            BlockState state = this.level.getBlockState(pos.relative(Direction.DOWN, i));
            if (state.getFluidState().is(FluidTags.WATER))
                depth += 1.0f;
            else if (!state.isAir())
                break;
        }

        for (int i = 1; i < 3; ++i) {
            BlockState state = this.level.getBlockState(pos.relative(Direction.UP, i));
            if (state.getFluidState().is(FluidTags.WATER))
                depth += 1.0f;
            else if (!state.isAir())
                break;
        }

        return depth;
    }

    protected boolean canFitInWater(Vec3 pos) {
        final float height = this.getDimensions(Pose.STANDING).height;

        final BlockPos originalPos = new BlockPos(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));
        return BlockPos.betweenClosedStream(this.getDimensions(Pose.STANDING).makeBoundingBox(pos).inflate(-0.05))
                .filter(checkPos -> checkPos.getY() == originalPos.getY())
                .allMatch(blockPos -> getWaterDepth(blockPos) >= height);
    }

    protected boolean adjacentToLand(BlockPos pos) {
        return Direction.Plane.HORIZONTAL.stream().map(pos::relative).anyMatch(blockPos -> {
            if (!this.level.getBlockState(blockPos.above()).isAir())
                return false;
            return this.level.getBlockState(blockPos).isCollisionShapeFullBlock(this.level, blockPos);
        });
    }

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            this.maxUpStep = this.isInWater() ? 1.05f : 0.7f;

            boolean animateSwim = this.isInWater()
                    && this.canFitInWater(this.position());

            if (this.isEffectiveAi() && animateSwim) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }

            if (animateSwim) {
                this.setPose(Pose.SWIMMING);
            } else {
                this.setPose(Pose.STANDING);
            }
        }
    }

    boolean wantsToSwim() {
        LivingEntity livingentity = this.getTarget();
        if (livingentity == null)
            return true;
        if (livingentity.isInWater())
            return true;
        if (livingentity.isPassenger() && livingentity.getVehicle().isInWater())
            return true;
        return false;
    }

    static class AquaticMoveControl extends MoveControl {
        private final AbstractAquaticEntity aquaticEntity;

        public AquaticMoveControl(AbstractAquaticEntity p_32433_) {
            super(p_32433_);
            this.aquaticEntity = p_32433_;
        }

        public void tick() {
            aquaticEntity.updateSwimming();

            LivingEntity livingentity = this.aquaticEntity.getTarget();
            if (this.aquaticEntity.isSwimming()) {
                if (livingentity != null && livingentity.getY() > this.aquaticEntity.getY()) {
                    double dx = livingentity.getX() - this.aquaticEntity.getX();
                    double dz = livingentity.getZ() - this.aquaticEntity.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    this.aquaticEntity.setDeltaMovement(this.aquaticEntity.getDeltaMovement().add(dx / dist * 0.02D, 0.04D, dz / dist * 0.02D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || this.aquaticEntity.getNavigation().isDone()) {
                    this.aquaticEntity.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.aquaticEntity.getX();
                double d1 = this.wantedY - this.aquaticEntity.getY();
                double d2 = this.wantedZ - this.aquaticEntity.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.aquaticEntity.setYRot(this.rotlerp(this.aquaticEntity.getYRot(), f, 90.0F));
                this.aquaticEntity.yBodyRot = this.aquaticEntity.getYRot();
                float f1 = (float)(this.speedModifier * this.aquaticEntity.getAttributeValue(ForgeMod.SWIM_SPEED.get()));
                float f2 = Mth.lerp(0.125F, this.aquaticEntity.getSpeed(), f1);
                this.aquaticEntity.setSpeed(f2 * 1.05f);
                this.aquaticEntity.setDeltaMovement(this.aquaticEntity.getDeltaMovement().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
            } else {
                super.tick();
            }
        }
    }

    static class GoToWaterGoal extends Goal {
        private final AbstractAquaticEntity mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;

        public GoToWaterGoal(AbstractAquaticEntity entity, double speed) {
            this.mob = entity;
            this.speedModifier = speed;
            this.level = entity.level;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.mob.isInWater()) {
                return false;
            } else if (!mob.wantsToSwim()) {
                return false;
            } else if (this.mob.getTarget() != null) {
                return false;
            } else {
                Vec3 vec3 = this.getWaterPos();
                if (vec3 == null) {
                    return false;
                } else {
                    this.wantedX = vec3.x;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z;
                    return true;
                }
            }
        }

        public boolean canContinueToUse() {
            if (!mob.wantsToSwim() && this.mob.getTarget() != null)
                return false;

            return !this.mob.getNavigation().isDone();
        }

        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            Random random = this.mob.getRandom();
            BlockPos blockpos = this.mob.blockPosition();

            for(int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }

            return null;
        }
    }

    static class SinkFromSurfaceGoal extends Goal {
        private final AbstractAquaticEntity mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;

        public SinkFromSurfaceGoal(AbstractAquaticEntity entity, double speed) {
            this.mob = entity;
            this.speedModifier = speed;
            this.level = entity.level;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.mob.getTarget() != null) {
                return false;
            } else if (!this.mob.isInWater()) {
                return false;
            } else if (!level.getBlockState(this.mob.blockPosition()).isAir()) {
                return false;
            } else if (!this.mob.canFitInWater(this.mob.position())) {
                return false;
            } else {
                this.wantedX = this.mob.getX();
                this.wantedY = this.mob.getY() - 1.0;
                this.wantedZ = this.mob.getZ();
                return true;
            }
        }

        public boolean canContinueToUse() {
            if (this.mob.getTarget() != null) {
                return false;
            }

            return !this.mob.getNavigation().isDone();
        }

        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            Random random = this.mob.getRandom();
            BlockPos blockpos = this.mob.blockPosition();

            for(int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (this.level.getBlockState(blockpos1).is(Blocks.WATER)) {
                    return Vec3.atBottomCenterOf(blockpos1);
                }
            }

            return null;
        }
    }
}
