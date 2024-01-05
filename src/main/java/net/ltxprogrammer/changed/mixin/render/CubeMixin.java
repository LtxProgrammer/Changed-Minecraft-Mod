package net.ltxprogrammer.changed.mixin.render;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(ModelPart.Cube.class)
public abstract class CubeMixin implements CubeExtender {
    @Shadow @Final private ModelPart.Polygon[] polygons;

    @Override
    public Vector3f getVisualMin() {
        return this.polygons[1].vertices[0].pos;
    }

    @Override
    public Vector3f getVisualMax() {
        return this.polygons[0].vertices[3].pos;
    }

    @Override
    public UVPair getUV(Vector3f cubeSurfaceNormal) {
        ModelPart.Polygon surface = null;
        float bestMatch = -1.0f;

        float xLerp = 0.0f;
        float yLerp = 0.0f;

        for (ModelPart.Polygon polygon : this.polygons) {
            var polyNormal = polygon.normal;

            float thisMatch = polyNormal.dot(cubeSurfaceNormal);
            if (thisMatch > bestMatch) {
                surface = polygon;
                bestMatch = thisMatch;

                if (Mth.abs(polyNormal.x()) > Mth.abs(polyNormal.y()) && Mth.abs(polyNormal.x()) > Mth.abs(polyNormal.z())) {
                    xLerp = cubeSurfaceNormal.z() * 0.5f + 0.5f;
                    yLerp = cubeSurfaceNormal.y() * 0.5f + 0.5f;
                } else if (Mth.abs(polyNormal.y()) > Mth.abs(polyNormal.x()) && Mth.abs(polyNormal.y()) > Mth.abs(polyNormal.z())) {
                    xLerp = cubeSurfaceNormal.x() * 0.5f + 0.5f;
                    yLerp = cubeSurfaceNormal.z() * 0.5f + 0.5f;
                } else {
                    xLerp = cubeSurfaceNormal.x() * 0.5f + 0.5f;
                    yLerp = cubeSurfaceNormal.y() * 0.5f + 0.5f;
                }
            }
        }

        if (surface == null) {
            Changed.LOGGER.warn("Null surface encountered for given normal {}, with {} polygons", cubeSurfaceNormal, this.polygons.length);
            return new UVPair(0, 0);
        }

        float uX = Mth.lerp(xLerp, surface.vertices[0].u, surface.vertices[1].u);
        float uY = Mth.lerp(xLerp, surface.vertices[3].u, surface.vertices[2].u);
        float vX = Mth.lerp(xLerp, surface.vertices[0].v, surface.vertices[1].v);
        float vY = Mth.lerp(xLerp, surface.vertices[3].v, surface.vertices[2].v);

        return new UVPair(Mth.lerp(yLerp, uX, uY), Mth.lerp(yLerp, vX, vY));
    }
}
