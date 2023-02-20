package net.ltxprogrammer.changed.client.renderer.model;

import net.minecraft.world.entity.HumanoidArm;

public enum CorrectorType {
    HAIR(false),
    LEFT_ARM(true),
    RIGHT_ARM(true);
    private final boolean isArm;

    CorrectorType(boolean isArm) {
        this.isArm = isArm;
    }

    public boolean isArm() {
        return isArm;
    }

    public static CorrectorType fromArm(HumanoidArm arm) {
        return switch (arm) {
            case LEFT -> LEFT_ARM;
            case RIGHT -> RIGHT_ARM;
        };
    }
}
