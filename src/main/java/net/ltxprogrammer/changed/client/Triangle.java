package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;

import javax.annotation.Nullable;

public class Triangle {
    public final ModelPart.Vertex[] vertices;
    public final Vector3f normal;

    public Triangle(ModelPart.Vertex[] vertices, Vector3f normal) {
        this.vertices = vertices;
        this.normal = normal;
    }

    public void compile(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        Vector3f vector3f = this.normal.copy();
        vector3f.transform(matrix3f);
        float nx = vector3f.x();
        float ny = vector3f.y();
        float nz = vector3f.z();

        // VertexConsumer is in quad mode, thus expect 4 points
        for(ModelPart.Vertex vertex : this.vertices) {
            float f3 = vertex.pos.x() / 16.0F;
            float f4 = vertex.pos.y() / 16.0F;
            float f5 = vertex.pos.z() / 16.0F;
            Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
            vector4f.transform(matrix4f);

            consumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, packedOverlay, packedLight, nx, ny, nz);
            if (vertex == this.vertices[2])
                consumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.u, vertex.v, packedOverlay, packedLight, nx, ny, nz);
        }
    }

    public static class Definition {
        @Nullable
        private final String comment;
        public Vector3f p1, p2, p3;
        private final UVPair uv1, uv2, uv3;

        public Definition(@Nullable String comment, Vector3f p1, Vector3f p2, Vector3f p3, UVPair uv1, UVPair uv2, UVPair uv3) {
            this.comment = comment;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.uv1 = uv1;
            this.uv2 = uv2;
            this.uv3 = uv3;
        }

        public Triangle bake(int textureWidth, int textureHeight) {
            ModelPart.Vertex[] vertices = new ModelPart.Vertex[]{
                    new ModelPart.Vertex(p1, uv1.u() / (float)textureWidth, uv1.v() / (float)textureHeight),
                    new ModelPart.Vertex(p2, uv2.u() / (float)textureWidth, uv2.v() / (float)textureHeight),
                    new ModelPart.Vertex(p3, uv3.u() / (float)textureWidth, uv3.v() / (float)textureHeight)
            };

            Vector3f p1p2 = p2.copy();
            Vector3f p1p3 = p3.copy();
            p1p2.sub(p1);
            p1p3.sub(p1);
            p1p3.cross(p1p2);
            p1p3.normalize();

            return new Triangle(vertices, p1p3);
        }
    }
}
