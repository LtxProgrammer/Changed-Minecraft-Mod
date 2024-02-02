package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.Set;

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

    @Unique
    private static final ModelPart.Vertex NULL_VERTEX = new ModelPart.Vertex(0, 0, 0, 0, 0);

    @Override
    public void removeSides(Set<Direction> directions) {
        for (var dir : directions) {
            Vector3f step = dir.getAxis() == Direction.Axis.Y ? dir.getOpposite().step() : dir.step();
            for (ModelPart.Polygon polygon : polygons)
                if (polygon.normal.equals(step))
                    Arrays.fill(polygon.vertices, NULL_VERTEX);
        }
    }

    @Override
    public ModelPart.Polygon[] getPolygons() {
        return polygons;
    }

    @Override
    public void copyPolygonsFrom(ModelPart.Cube cube) {
        ModelPart.Polygon[] otherPoly = ((CubeExtender)cube).getPolygons();
        for (int i = 0; i < otherPoly.length; ++i) {
            ModelPart.Vertex[] nVertices = new ModelPart.Vertex[] {
                    otherPoly[i].vertices[0],
                    otherPoly[i].vertices[1],
                    otherPoly[i].vertices[2],
                    otherPoly[i].vertices[3]
            };

            this.polygons[i] = new ModelPart.Polygon(nVertices, 0.0f, 0.0f, 0.0f, 0.0f,
                    1.0f, 1.0f, false, Direction.getNearest(otherPoly[i].normal.x(), otherPoly[i].normal.y(), otherPoly[i].normal.z()));

            for (int v = 0; v < this.polygons[i].vertices.length; ++v) {
                final ModelPart.Vertex otherVert = otherPoly[i].vertices[v];

                // Deep copy
                this.polygons[i].vertices[v] = otherVert.remap(otherVert.u, otherVert.v);
            }
        }
    }
}
