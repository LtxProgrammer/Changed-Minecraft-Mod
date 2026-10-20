package net.ltxprogrammer.changed.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.FlyingAnimal;

public class FlyingOrWalkingMoveControl<T extends PathfinderMob & FlyingAnimal> extends MoveControl {
    private final int maxTurn;
    private final boolean hoversInPlace;
    protected final FlyingAnimal flyingCapable;

    public FlyingOrWalkingMoveControl(T entity, int maxTurn, boolean hoversInPlace) {
        super(entity);
        this.maxTurn = maxTurn;
        this.hoversInPlace = hoversInPlace;
        this.flyingCapable = entity;
    }

    @Override
    public void tick() {
        if (!flyingCapable.isFlying()) {
            super.tick();
            this.mob.setNoGravity(false);
        } else {
            this.mob.resetFallDistance();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().multiply(1d, 0.95d, 1d));

            if (this.operation != Operation.WAIT) {
                this.operation = Operation.WAIT;
                this.mob.setNoGravity(true);
                double deltaX = this.wantedX - this.mob.getX();
                double deltaY = this.wantedY - this.mob.getY();
                double deltaZ = this.wantedZ - this.mob.getZ();
                double distanceSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
                if (distanceSqr < (double) 2.5000003E-7F) {
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    return;
                }

                float horizontalAngle = (float) (Mth.atan2(deltaZ, deltaX) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), horizontalAngle, 90.0F));
                float speed;
                if (this.mob.onGround()) {
                    speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                } else {
                    speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                }

                this.mob.setSpeed(speed);
                double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                if (Math.abs(deltaY) > (double) 1.0E-5F || Math.abs(horizontalDistance) > (double) 1.0E-5F) {
                    float verticalAngle = (float) (-(Mth.atan2(deltaY, horizontalDistance) * (double) (180F / (float) Math.PI)));
                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), verticalAngle, (float) this.maxTurn));
                    this.mob.setYya(deltaY > (double) 0.0F ? speed : -speed);
                }
            } else {
                if (!this.hoversInPlace) {
                    this.mob.setNoGravity(false);
                }

                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }
        }
    }
}
