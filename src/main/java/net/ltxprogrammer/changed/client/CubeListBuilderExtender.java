package net.ltxprogrammer.changed.client;

import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.List;

public interface CubeListBuilderExtender {
    CubeListBuilder removeLastFaces(Direction... directions);

    void addTriangle(String comment, Vector3f p1, UVPair uv1,
                     Vector3f p2, UVPair uv2,
                     Vector3f p3, UVPair uv3);

    default void addTriangle(String comment, Vector3f p1, float u1, float v1,
                     Vector3f p2, float u2, float v2,
                     Vector3f p3, float u3, float v3) {
        addTriangle(comment, p1, new UVPair(u1, v1), p2, new UVPair(u2, v2), p3, new UVPair(u3, v3));
    }

    default void addTriangle(String comment, float x1, float y1, float z1, float u1, float v1,
                     float x2, float y2, float z2, float u2, float v2,
                     float x3, float y3, float z3, float u3, float v3) {
        addTriangle(comment, new Vector3f(x1, y1, z1), u1, v1, new Vector3f(x2, y2, z2), u2, v2, new Vector3f(x3, y3, z3), u3, v3);
    }

    default void addTriangle(float x1, float y1, float z1, float u1, float v1,
                     float x2, float y2, float z2, float u2, float v2,
                     float x3, float y3, float z3, float u3, float v3) {
        addTriangle(null, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3);
    }

    @Nullable
    List<Triangle.Definition> getTriangles();
}
