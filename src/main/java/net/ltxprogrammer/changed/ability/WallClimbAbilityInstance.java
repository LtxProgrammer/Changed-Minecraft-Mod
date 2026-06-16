package net.ltxprogrammer.changed.ability;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

public class WallClimbAbilityInstance extends AbstractAbilityInstance {
    public static final float LOOK_CLAMP = 0.995f;
    public static final Vec3 UP = new Vec3(0.0d, 1.0d, 0.0d);
    public static final Vec3 DOWN = UP.reverse();
    public static final Vec3 FORWARD = new Vec3(0.0d, 0.0d, 1.0d);
    public static final Vec3 RIGHT = new Vec3(1.0d, 0.0d, 0.0d);
    public static final Matrix3f GLOBAL_VIEW_SPACE = new Matrix3f(
            (float) RIGHT.x, (float) UP.x, (float) FORWARD.x,
            (float) RIGHT.y, (float) UP.y, (float) FORWARD.y,
            (float) RIGHT.z, (float) UP.z, (float) FORWARD.z
    );
    public static final List<Vec3> NORMALS = List.of(
            UP,
            FORWARD,
            RIGHT,

            UP.add(FORWARD).normalize(),
            UP.add(RIGHT).normalize(),
            UP.add(RIGHT).add(FORWARD).normalize()
    );

    /// Vector that points away from the surface, treated as the new up direction +Y
    protected Vec3 surfaceNormal = UP;
    protected Vec3 pushNormal = UP;
    protected Vec3 lastSurfaceNormal = UP;
    /// Vector that points along the surface, treated as the new right direction +X
    protected Vec3 planeRight = Vec3.ZERO;
    /// Vector that points along the surface, treated as the new forward direction +Z
    protected Vec3 planeForward = Vec3.ZERO;
    /// Points that the entity's hitbox should touch on the border
    protected final List<Vec3> pushPositions = new ReferenceArrayList<>();

    protected boolean isActive = false;
    protected boolean freeTraversal = false;

    protected float effectiveRoll = 0.0f;
    protected float effectiveRollO = 0.0f;

    public float getEffectiveRoll(float partialTick) {
        return Mth.lerp(partialTick, effectiveRollO, effectiveRoll);
    }

    public WallClimbAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isTransitioning() {
        return false;
    }

    public boolean shouldAnimateCamera() {
        return isActive || isTransitioning();
    }

    public Vec3 getSurfaceNormal() {
        return surfaceNormal;
    }

    @Override
    public boolean canUse() {
        return surfaceNormal != Vec3.ZERO;
    }

    @Override
    public boolean canKeepUsing() {
        return false;
    }

    @Override
    public void startUsing() {
        var level = entity.getLevel();
        if (!level.isClientSide() || this.pushPositions.isEmpty())
            return;

        this.isActive = !isActive;
        if (this.isActive) {
            this.effectiveRoll = 0.0f;
            this.effectiveRollO = 0.0f;
        }

        CompoundTag tag = new CompoundTag();
        tag.putBoolean("active", isActive);
        this.sendPayload(tag);
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        isActive = tag.getBoolean("active");
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        tag.putBoolean("active", isActive);
    }

    @Override
    public void acceptPayload(CompoundTag tag) {
        super.acceptPayload(tag);
        isActive = tag.getBoolean("active");
    }

    @Override
    public void tick() {

    }

    protected record BlockCollision(BlockPos.MutableBlockPos position, int distanceManhattan, VoxelShape shape) {}

    protected Iterable<BlockCollision> getBlockCollisions(CollisionGetter level, @Nullable Entity collidingEntity, AABB sampleBox, AABB distanceBox) {
        BoundingBox boundingBox = EntityUtil.roundAABB(distanceBox);

        return () -> {
            return new BlockCollisions<>(level, collidingEntity, sampleBox, false, (position, shape) -> {
                if (boundingBox.isInside(position))
                    return new BlockCollision(position, 0, shape);

                var distance = BlockPos.betweenClosedStream(boundingBox).mapToInt(testPos -> testPos.distManhattan(position)).min();
                return new BlockCollision(position, distance.orElse(0), shape);
            });
        };
    }

    @Override
    public void tickIdle() {
        super.tickIdle();

        var self = entity.getEntity();
        var level = entity.getLevel();

        if (level.isClientSide) {
            var lookAngle = self.getLookAngle();
            this.updateSurfaceVectors(lookAngle);
            this.updateViewAngles(lookAngle);

            self.setDiscardFriction(isActive);

            if (isActive) {
                self.setNoGravity(true);
                self.setPose(Pose.SWIMMING);
                this.pushPull(pushPositions);
                this.transitionVelocityAndView(lastSurfaceNormal, surfaceNormal);
                this.friction();
            }
        } else {
            self.setDiscardFriction(isActive);

            if (isActive) {
                self.setNoGravity(true);
                self.setPose(Pose.SWIMMING);
            }
        }
    }

    protected Vec3 closestPointInBox(Vec3 point, AABB box) {
        return new Vec3(
                Mth.clamp(point.x, box.minX, box.maxX),
                Mth.clamp(point.y, box.minY, box.maxY),
                Mth.clamp(point.z, box.minZ, box.maxZ)
        );
    }

    protected Vec3 lerpVector(double alpha, Vec3 a, Vec3 b) {
        if (a.equals(b))
            return a;
        if (alpha == 0.0d)
            return a;
        if (alpha == 1.0d)
            return b;

        return new Vec3(
                Mth.lerp(alpha, a.x, b.x),
                Mth.lerp(alpha, a.y, b.y),
                Mth.lerp(alpha, a.z, b.z)
        ).normalize();
    }

    protected void updateSurfaceVectors(Vec3 lookAngle) {
        var self = entity.getEntity();
        var level = entity.getLevel();

        var entityBox = self.getDimensions(Pose.SWIMMING).makeBoundingBox(self.position());
        var upAngle = self.getUpVector(1.0f);
        var checkBox = entityBox.inflate(1.0);
        var clampBox = entityBox.inflate(-0.01); // Clamp the closest entity point so that a normal can be found when touching the surface
        // TODO replace clampBox with effective way to get surface normal
        // TODO find attachment point on surface

        Vec3 normalSum = Vec3.ZERO; // Sums up normal vectors to find average normal
        Vec3 normalBias = Vec3.ZERO; // Finds the closest normal that aligns to the player's upAngle
        Vec3 positionSum = Vec3.ZERO; // Finds the closest normal that aligns to the player's upAngle
        int positionSumCount = 0;
        int closestDistance = 99999;

        this.pushPositions.clear();

        this.freeTraversal = false;
        this.lastSurfaceNormal = this.surfaceNormal;

        if (entity.hasFeature(ChangedVariantFeatures.CLIMB_COBWEB.get())) {
            boolean nearCobweb = level.getBlockStatesIfLoaded(entityBox.inflate(0.5)).anyMatch(blockState -> blockState.is(Blocks.COBWEB));
            if (nearCobweb) { // Surface vectors will equal the player's up direction. Allows the player to freely climb through cobweb
                this.surfaceNormal = lerpVector(0.3d, this.surfaceNormal, upAngle);
                this.freeTraversal = true;
            }
        }

        // Find all collision shapes that are adjacent to the entity's bounding box
        for (var blockCollisionPair : getBlockCollisions(level, self, checkBox, entityBox)) {
            int distanceManhattan = blockCollisionPair.distanceManhattan;
            if (distanceManhattan == 0 || distanceManhattan > closestDistance)
                continue;

            VoxelShape blockCollision = blockCollisionPair.shape;
            if (blockCollision.isEmpty())
                continue;

            var collisionCenter = blockCollision.bounds().getCenter();
            var closestEntityPoint = closestPointInBox(collisionCenter, clampBox);
            var closestPoint = blockCollision.closestPointTo(closestEntityPoint);
            if (closestPoint.isEmpty())
                continue;

            var surfaceNormal = closestEntityPoint.subtract(closestPoint.get()).normalize();
            if (distanceManhattan < closestDistance) { // Working with a block that is closer than previous blocks, reset
                normalSum = Vec3.ZERO;
                normalBias = Vec3.ZERO;
                positionSum = Vec3.ZERO;
                positionSumCount = 0;
                closestDistance = distanceManhattan;
                this.pushPositions.clear();
            }

            normalSum = normalSum.add(surfaceNormal);
            positionSum = positionSum.add(closestPoint.get());
            positionSumCount++;
            this.pushPositions.add(closestPoint.get());

            if (normalBias == Vec3.ZERO || surfaceNormal.dot(upAngle) > normalBias.dot(upAngle)) {
                normalBias = surfaceNormal;
            }
        }

        normalSum = normalSum.normalize();
        if (normalSum == Vec3.ZERO)
            normalSum = normalBias;

        if (this.freeTraversal) {
            if (positionSumCount > 0)
                this.pushPositions.add(positionSum.scale(1.0d / positionSumCount));
            if (normalSum == Vec3.ZERO)
                pushNormal = lerpVector(0.3d, this.pushNormal, UP);
            else
                pushNormal = lerpVector(0.3d, this.pushNormal, normalSum);
        } else if (normalSum == Vec3.ZERO) {
            // No surface normals present
            this.surfaceNormal = lerpVector(0.3d, this.surfaceNormal, UP);
            this.pushNormal = this.surfaceNormal;
            this.pushPositions.clear();
            if (this.isActive) {
                this.isActive = false;

                CompoundTag tag = new CompoundTag();
                tag.putBoolean("active", false);
                this.sendPayload(tag);
            }
        } else {
            this.surfaceNormal = lerpVector(0.3d, this.surfaceNormal, normalSum);
            this.pushNormal = this.surfaceNormal;
            this.pushPositions.add(positionSum.scale(1.0d / positionSumCount));
        }

        this.planeRight = lookAngle.cross(surfaceNormal).normalize();
        this.planeForward = this.surfaceNormal.cross(planeRight);
    }

    protected void updateViewAngles(Vec3 lookAngle) {
        var self = entity.getEntity();

        this.effectiveRollO = this.effectiveRoll;

        {
            Vec3 localUpVector = this.planeRight.cross(lookAngle);
            Vec3 rightVector = this.planeRight;

            Matrix3f rotationMatrix = new Matrix3f(
                    (float) rightVector.x, (float) localUpVector.x, (float) lookAngle.x,
                    (float) rightVector.y, (float) localUpVector.y, (float) lookAngle.y,
                    (float) rightVector.z, (float) localUpVector.z, (float) lookAngle.z
            );

            CameraUtil.decomposeZXY(rotationMatrix, (targetPitch, targetYaw, targetRoll) -> {
                this.effectiveRoll = targetRoll * Mth.RAD_TO_DEG;

                while (this.effectiveRoll - this.effectiveRollO > 180.0f)
                    this.effectiveRoll -= 360.0f;
                while (this.effectiveRoll - this.effectiveRollO < -180.0f)
                    this.effectiveRoll += 360.0f;
            });
        }
    }

    protected void pushPull(List<Vec3> targetPositions) {
        if (targetPositions.isEmpty())
            return;

        var self = entity.getEntity();

        var entityBox = self.getBoundingBox();
        var tooCloseBox = entityBox.inflate(0.25);
        var paddedBox = entityBox.inflate(0.4);

        Vec3 closestTargetPosition = Vec3.ZERO;
        Vec3 closestEntityPoint = Vec3.ZERO;
        Vec3 closestTooClosePoint = Vec3.ZERO;
        Vec3 closestPaddedPoint = Vec3.ZERO;
        double closestDistance = 999999.0d;

        for (var targetPosition : targetPositions) {
            var closestPoint = closestPointInBox(targetPosition, entityBox);
            var distanceSqr = closestPoint.distanceToSqr(targetPosition);
            if (distanceSqr < closestDistance) {
                closestTargetPosition = targetPosition;
                closestEntityPoint = closestPoint;
                closestTooClosePoint = closestPointInBox(targetPosition, tooCloseBox);
                closestPaddedPoint = closestPointInBox(targetPosition, paddedBox);
                closestDistance = distanceSqr;
            }
        }

        if (closestTooClosePoint.equals(closestTargetPosition)) {
            // Too close, push away on normal
            var scale = 1.0d - closestEntityPoint.distanceTo(closestTooClosePoint);
            self.move(MoverType.SELF, this.pushNormal.scale(0.2d * scale));
            // Slow movement that is parallel to normal
            self.setDeltaMovement(self.getDeltaMovement().multiply(
                    1.0d - Math.abs(this.pushNormal.x * 0.91f),
                    1.0d - Math.abs(this.pushNormal.y * 0.91f),
                    1.0d - Math.abs(this.pushNormal.z * 0.91f)
            ));
        } else if (!closestPaddedPoint.equals(closestTargetPosition) && !this.freeTraversal) {
            // Too far, pull along on normal
            var scale = closestTargetPosition.distanceTo(closestPaddedPoint);
            self.move(MoverType.SELF, this.pushNormal.scale(-0.4d * scale));
            // Slow movement that is parallel to normal
            self.setDeltaMovement(self.getDeltaMovement().multiply(
                    1.0d - Math.abs(this.pushNormal.x * 0.91f),
                    1.0d - Math.abs(this.pushNormal.y * 0.91f),
                    1.0d - Math.abs(this.pushNormal.z * 0.91f)
            ));
        }
    }

    protected void transitionVelocityAndView(Vec3 previousNormal, Vec3 currentNormal) {
        if (freeTraversal)
            return;
        if (previousNormal.equals(currentNormal))
            return;
        var cross = previousNormal.cross(currentNormal).normalize();
        if (cross == Vec3.ZERO)
            return;

        var self = entity.getEntity();

        var deltaMovement = self.getDeltaMovement();
        var dot = previousNormal.dot(currentNormal);

        var rotatedDeltaMovement = new Matrix3f().rotate(
                (float) Math.acos(dot),
                (float) cross.x,
                (float) cross.y,
                (float) cross.z
        ).transform(new Vector3f((float) deltaMovement.x, (float) deltaMovement.y, (float) deltaMovement.z));

        self.setDeltaMovement(rotatedDeltaMovement.x, rotatedDeltaMovement.y, rotatedDeltaMovement.z);
    }

    protected void friction() {
        var self = entity.getEntity();

        var deltaMovement = self.getDeltaMovement();
        self.setDeltaMovement(deltaMovement.scale(0.91f));
    }

    public Vector3f turnOnWall(Vec3 currentLookAngle, double dx, double dy) {
        this.planeRight = currentLookAngle.cross(surfaceNormal).normalize();
        this.planeForward = this.surfaceNormal.cross(planeRight);

        var rotationMatrix = new Matrix3f();
        rotationMatrix.rotate(Mth.DEG_TO_RAD * (float) -dx, (float) this.surfaceNormal.x, (float) this.surfaceNormal.y, (float) this.surfaceNormal.z);
        var nextLookRef = rotationMatrix.transform(new Vector3f(
                (float) this.planeForward.x,
                (float) this.planeForward.y,
                (float) this.planeForward.z
        ));
        rotationMatrix.rotate(Mth.DEG_TO_RAD * (float) -dy, (float) this.planeRight.x, (float) this.planeRight.y, (float) this.planeRight.z);
        var nextLookAngle = rotationMatrix.transform(new Vector3f(
                (float) currentLookAngle.x,
                (float) currentLookAngle.y,
                (float) currentLookAngle.z
        ));

        var dot = nextLookAngle.dot((float) this.surfaceNormal.x, (float) this.surfaceNormal.y, (float) this.surfaceNormal.z);
        if (dot > LOOK_CLAMP || dot < -LOOK_CLAMP) {
            nextLookAngle.sub(
                    (float) this.surfaceNormal.x,
                    (float) this.surfaceNormal.y,
                    (float) this.surfaceNormal.z
            );

            var nextLookRefRight = nextLookRef.cross(
                    (float) this.surfaceNormal.x,
                    (float) this.surfaceNormal.y,
                    (float) this.surfaceNormal.z,
                    new Vector3f()
            );

            new Matrix3f().rotate(
                    (float) Math.asin(Mth.sign(dot) * LOOK_CLAMP),
                    nextLookRefRight.x,
                    nextLookRefRight.y,
                    nextLookRefRight.z
            ).transform(nextLookRef, nextLookAngle);
        }

        return nextLookAngle;
    }

    public Vec3 walkOnWall(Vec3 inputAngle, float speed) {
        return surfaceNormal.scale(inputAngle.y)
                .add(planeForward.scale(inputAngle.z))
                .add(planeRight.scale(-inputAngle.x))
                .scale(speed);
    }

    @Override
    public void stopUsing() {

    }
}
