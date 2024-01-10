package net.ltxprogrammer.changed.client;

import net.minecraft.world.phys.Vec3;

public interface CameraExtender {
    void setCameraPosition(Vec3 position);
    Vec3 getCameraPosition();
    Vec3 getFacingDirection();
    Vec3 getUpDirection();
    Vec3 getLeftDirection();
}
