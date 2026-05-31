package net.ltxprogrammer.changed.entity.beast;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
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

    class NamedLookControl extends LookControl {
        protected final String name;

        public NamedLookControl(Mob mob, String name) {
            super(mob);
            this.name = name;
        }

        @Override
        protected boolean resetXRotOnTick() {
            return false;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
