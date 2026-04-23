package net.ltxprogrammer.changed.entity.beast;

import net.minecraft.world.phys.Vec3;

public interface TripleHeadedEntity extends DoubleHeadedEntity {
    float getLeftHeadYRot(float partialTicks);
    float getLeftHeadXRot(float partialTicks);

    @Override
    default float getHead2YRot(float partialTicks) {
        return getLeftHeadYRot(partialTicks);
    }

    @Override
    default float getHead2XRot(float partialTicks) {
        return getLeftHeadXRot(partialTicks);
    }

    default float getLeftHeadYRot() {
        return DoubleHeadedEntity.super.getHead2YRot();
    }

    default float getLeftHeadXRot() {
        return DoubleHeadedEntity.super.getHead2XRot();
    }

    void setLeftHeadYRot(float value);
    void setLeftHeadXRot(float value);

    @Override
    default void setHead2YRot(float value) {
        setLeftHeadYRot(value);
    }

    @Override
    default void setHead2XRot(float value) {
        setLeftHeadXRot(value);
    }

    Vec3 getLookAngleLeft();

    @Override
    default Vec3 getLookAngle2() {
        return getLookAngleLeft();
    }

    void lerpLeftHeadTo(float yRot, float xRot, int steps);

    @Override
    default void lerpHead2To(float yRot, float xRot, int steps) {
        lerpLeftHeadTo(yRot, xRot, steps);
    }

    float getRightHeadYRot(float partialTicks);
    float getRightHeadXRot(float partialTicks);

    default float getRightHeadYRot() {
        return getRightHeadYRot(1.0f);
    }
    default float getRightHeadXRot() {
        return getRightHeadXRot(1.0f);
    }

    void setRightHeadYRot(float value);
    void setRightHeadXRot(float value);

    Vec3 getLookAngleRight();

    void lerpRightHeadTo(float yRot, float xRot, int steps);
}
