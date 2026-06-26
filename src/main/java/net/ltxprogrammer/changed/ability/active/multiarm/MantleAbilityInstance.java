package net.ltxprogrammer.changed.ability.active.multiarm;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MantleAbilityInstance extends AbstractAbilityInstance {
    protected int climbTicks = 0;
    protected Vec3 startingPos = Vec3.ZERO;
    protected Vec3 endPos = Vec3.ZERO;
    protected AABB verticalBox = new AABB(BlockPos.ZERO);
    protected AABB targetBox = new AABB(BlockPos.ZERO);

    public MantleAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public AbstractAbility.UseType getUseType() {
        return AbstractAbility.UseType.HOLD;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    @Override
    public void startUsing() {
        tick();
    }

    protected AABB getBoundingBoxForPose(Pose pose) {
        var self = entity.getEntity();

        EntityDimensions entitydimensions = self.getDimensions(pose);
        float f = entitydimensions.width / 2.0F;
        Vec3 vec3 = new Vec3(self.getX() - (double)f, self.getY(), self.getZ() - (double)f);
        Vec3 vec31 = new Vec3(self.getX() + (double)f, self.getY() + (double)entitydimensions.height, self.getZ() + (double)f);
        return new AABB(vec3, vec31);
    }

    @Override
    public void acceptPayload(CompoundTag tag) {
        super.acceptPayload(tag);

        this.climbTicks = tag.getInt("climbTicks");
        this.startingPos = new Vec3(
                tag.getDouble("sx"),
                tag.getDouble("sy"),
                tag.getDouble("sz")
        );
        this.endPos = new Vec3(
                tag.getDouble("ex"),
                tag.getDouble("ey"),
                tag.getDouble("ez")
        );
        this.getController().forceCooldown(ability.getCoolDown(entity));
    }

    @Override
    public void tick() {
        var self = entity.getEntity();
        var level = entity.getLevel();
        if (level.isClientSide)
            return;

        Direction facingDirection = self.getDirection();
        var standingBox = self.getBoundingBox();
        var minimumBox = getBoundingBoxForPose(Pose.SWIMMING);

        float mantleThresholdMax = 0.85f * (Mth.abs((float)self.getDeltaMovement().y) * 0.5f + 1.0f);
        float mantleThresholdMin = 0.55f;

        verticalBox = minimumBox.move(
                0.0,
                standingBox.getYsize() * mantleThresholdMax,
                0.0);
        targetBox = minimumBox.move(
                facingDirection.getStepX() * 0.75f,
                facingDirection.getStepY() + standingBox.getYsize() * mantleThresholdMax,
                facingDirection.getStepZ() * 0.75f);

        if (!level.noCollision(verticalBox) || !level.noCollision(targetBox))
            return;

        var targetCenter = targetBox.getCenter();
        var targetPos = new Vec3(
                targetCenter.x,
                targetBox.minY,
                targetCenter.z
        );
        var clip = level.clip(new ClipContext(
                targetPos,
                targetPos.subtract(
                        0.0d,
                        standingBox.getYsize() * (mantleThresholdMax - mantleThresholdMin),
                        0.0d
                ),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                self
        ));

        if (clip.isInside() || clip.getType() == HitResult.Type.MISS)
            return; // Max target position is clipped inside a block, or clip position is below minimum threshold

        // Target position and travel position is free
        climbTicks = 10;
        startingPos = self.position();
        endPos = clip.getLocation();
        self.causeFallDamage(self.fallDistance, /* Percent Damage */ 0.25F, ChangedDamageSources.MANTLE_STOP.source(level.registryAccess()));
        self.resetFallDistance();
        this.getController().forceCooldown(ability.getCoolDown(entity));

        CompoundTag tag = new CompoundTag();
        tag.putInt("climbTicks", climbTicks);
        tag.putDouble("sx", startingPos.x);
        tag.putDouble("sy", startingPos.y);
        tag.putDouble("sz", startingPos.z);
        tag.putDouble("ex", endPos.x);
        tag.putDouble("ey", endPos.y);
        tag.putDouble("ez", endPos.z);
        if (entity.getEntity() instanceof Player player)
            this.sendPayload(tag, player);
    }

    @Override
    public void tickIdle() {
        super.tickIdle();

        if (climbTicks > 0) {
            var self = entity.getEntity();
            var level = entity.getLevel();

            if (!level.isClientSide && (!level.noCollision(verticalBox) || !level.noCollision(targetBox))) {
                climbTicks = 0;
                CompoundTag tag = new CompoundTag();
                tag.putInt("climbTicks", climbTicks);
                if (entity.getEntity() instanceof Player player)
                    this.sendPayload(tag, player);
                return;
            }

            climbTicks--;
            if (self.position().y < endPos.y) {
                self.setPos(
                        self.position().x,
                        Mth.lerp((10 - climbTicks) / 5.0f, startingPos.y, endPos.y),
                        self.position().z
                ); // Travel up for ~5 ticks, allowing the entity to change their own Pose for level collision

                if (!level.noCollision(getBoundingBoxForPose(self.getPose()))) {
                    if (level.noCollision(getBoundingBoxForPose(Pose.CROUCHING)))
                        self.setPose(Pose.CROUCHING);
                    else
                        self.setPose(Pose.SWIMMING);
                }
            }

            self.setDeltaMovement(
                    (endPos.x - startingPos.x) * (1.0f / 15.0f),
                    0.0f,
                    (endPos.z - startingPos.z) * (1.0f / 15.0f)
            ); // Stop vertical velocity, and move horizontally to target
        }
    }

    @Override
    public void stopUsing() {

    }
}
