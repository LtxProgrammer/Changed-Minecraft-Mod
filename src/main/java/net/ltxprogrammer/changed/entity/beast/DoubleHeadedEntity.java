package net.ltxprogrammer.changed.entity.beast;

import net.minecraft.world.phys.Vec3;

public interface DoubleHeadedEntity {
    float getHead2YRot(float partialTicks);
    float getHead2XRot(float partialTicks);

    default float getHead2YRot() {
        return getHead2YRot(1.0f);
    }
    default float getHead2XRot() {
        return getHead2XRot(1.0f);
    }

    void setHead2YRot(float value);
    void setHead2XRot(float value);

    Vec3 getLookAngle2();

    void lerpHead2To(float yRot, float xRot, int steps);
}
