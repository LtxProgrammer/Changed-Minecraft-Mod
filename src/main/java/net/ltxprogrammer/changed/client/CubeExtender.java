package net.ltxprogrammer.changed.client;

import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.builders.UVPair;

public interface CubeExtender {
    Vector3f getVisualMin();
    Vector3f getVisualMax();
    UVPair getUV(Vector3f surfacePoint);
}
