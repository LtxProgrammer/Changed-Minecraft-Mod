package net.ltxprogrammer.changed.entity.robot;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class Roomba extends AbstractRobot {
    protected boolean isRotating = false;

    public Roomba(EntityType<? extends Roomba> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new WanderGoal(this));
        this.goalSelector.addGoal(3, new RotateToNextAxisGoal(this, 90F / 15));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.15D);
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        // Omitted
    }

    @Override
    public boolean isAffectedByWater() {
        return true;
    }

    @Override
    public SoundEvent getRunningSound() {
        return ChangedSounds.VACUUM;
    }

    @Override
    public int getRunningSoundDuration() {
        return 100;
    }

    @Override
    public float getMaxDamage() {
        return 40f;
    }

    @Override
    public ChargerType getChargerType() {
        return ChargerType.ROOMBA;
    }

    public double distanceFromBlock(BlockPos pos) {
        Vec3 blockPos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return blockPos.distanceTo(this.position());
    }

    public boolean shouldConsiderBlock(BlockPos pos) {
        double dist = distanceFromBlock(pos) - 0.5;
        double bbWidthAdj = this.getBbWidth();
        return dist < bbWidthAdj;
    }

    public BlockPos getFrontPos() {
        Vec3 angle = this.getForward();
        BlockPos pos = this.blockPosition().relative(Direction.getNearest(angle.x, angle.y, angle.z));
        if (shouldConsiderBlock(pos))
            return pos;
        return this.blockPosition();
    }

    public BlockPos getRightPos() {
        Vec3 angle = this.getForward();
        BlockPos pos = this.blockPosition().relative(Direction.getNearest(angle.x, angle.y, angle.z).getClockWise());
        if (shouldConsiderBlock(pos))
            return pos;
        return this.blockPosition();
    }

    public BlockPos getLeftPos() {
        Vec3 angle = this.getForward();
        BlockPos pos = this.blockPosition().relative(Direction.getNearest(angle.x, angle.y, angle.z).getCounterClockWise());
        if (shouldConsiderBlock(pos))
            return pos;
        return this.blockPosition();
    }

    public BlockPos getBackPos() {
        Vec3 angle = this.getForward();
        BlockPos pos = this.blockPosition().relative(Direction.getNearest(angle.x, angle.y, angle.z).getOpposite());
        if (shouldConsiderBlock(pos))
            return pos;
        return this.blockPosition();
    }

    public boolean canVisitBlock(BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos, CollisionContext.of(this))
                .isEmpty();
    }

    @Override
    public void tick() {
        super.tick();

        setYBodyRot(getYRot());
        setYHeadRot(getYRot());
        yBodyRotO = yRotO;
        yHeadRotO = yRotO;
    }

    public static class WanderGoal extends Goal {
        public final Roomba robot;

        public WanderGoal(Roomba robot) {
            this.robot = robot;
        }

        @Override
        public boolean canUse() {
            return this.robot.canVisitBlock(robot.getFrontPos()) && !robot.isRotating && !robot.isLowBattery();
        }

        @Override
        public void tick() {
            super.tick();
            this.robot.setYRot(robot.roundYRotToAxis(this.robot.getYRot()));
            Vec3 travelForward = this.robot.getForward().multiply(1, 0, 1).normalize();
            float speed = this.robot.getSpeed();

            this.robot.move(MoverType.SELF, travelForward.multiply(speed, speed, speed));
        }
    }

    public static class RotateToNextAxisGoal extends Goal {
        public final Roomba robot;
        public final float rotateSpeed;

        private float startYRot = 0f;
        private float goalYRot = 0f;

        private boolean positiveDir;

        public RotateToNextAxisGoal(Roomba robot, float rotateSpeed) {
            this.robot = robot;
            this.rotateSpeed = rotateSpeed;
        }

        public boolean shouldRotateClockwise() {
            return startYRot < goalYRot;
        }

        @Override
        public boolean canUse() {
            return !robot.canVisitBlock(robot.getFrontPos()) && !robot.isLowBattery();
        }

        @Override
        public boolean canContinueToUse() {
            return positiveDir == shouldRotateClockwise() && startYRot != goalYRot && !robot.isLowBattery();
        }

        @Override
        public void start() {
            super.start();
            startYRot = Mth.positiveModulo(robot.getYRot(), 360F);

            boolean right = robot.canVisitBlock(robot.getRightPos());
            boolean left = robot.canVisitBlock(robot.getLeftPos());

            if (right && left)
                goalYRot = startYRot + (robot.random.nextBoolean() ? 90F : -90F);
            else if (right)
                goalYRot = startYRot + 90F;
            else if (left)
                goalYRot = startYRot - 90F;
            else if (robot.canVisitBlock(robot.getBackPos()))
                goalYRot = startYRot + 180F;
            else
                goalYRot = startYRot + 90F; // Fallback, just rotate

            goalYRot = robot.roundYRotToAxis(goalYRot);

            positiveDir = shouldRotateClockwise();
            robot.isRotating = true;
        }

        @Override
        public void tick() {
            super.tick();
            if (shouldRotateClockwise())
                startYRot += rotateSpeed;
            else
                startYRot -= rotateSpeed;

            robot.setYRot(startYRot % 360F);
        }

        @Override
        public void stop() {
            super.stop();
            robot.setYRot(goalYRot % 360F);
            robot.isRotating = false;
        }
    }
}
