package net.ltxprogrammer.changed.effect.particle;


import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.latexparticles.LatexParticleRenderType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GlobbedLatexParticle extends Particle {
    protected final Entity entity;
    protected float scaleO = 0.0f;
    protected float scale = 0.0f;
    protected Quaternionf rotation = new Quaternionf();

    public GlobbedLatexParticle(ColoredEntityParticleOption option, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
        this.setSize(0.07f, 0.07f);
        this.entity = option.getEntity();

        this.lifetime = 20;

        this.gravity = 0.0f;
        this.hasPhysics = true;
        this.friction = 0.85f;

        this.xd = vx * 1;
        this.yd = vy * 1;
        this.zd = vz * 1;

        this.setColor(option.getColor().red(), option.getColor().green(), option.getColor().blue());
        this.rotation.rotateXYZ(
                (level.getRandom().nextFloat() * 2.0f - 1.0f) * 0.3926991f,
                (level.getRandom().nextFloat() * 2.0f - 1.0f) * 0.3926991f,
                (level.getRandom().nextFloat() * 2.0f - 1.0f) * 0.3926991f
        );
    }

    @Override
    public void tick() {
        super.tick();

        this.scaleO = this.scale;

        float scaledAge = this.age / (float)this.lifetime;
        float maxSizeTime = 4 / (float)this.lifetime;

        if (scaledAge < maxSizeTime) {
            this.scale = Mth.lerp(scaledAge / maxSizeTime, 0.0f, 1.0f);
        } else {
            this.scale = Mth.map(scaledAge, maxSizeTime, 1.0f, 1.0f, 0.0f);
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(partialTick, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(partialTick, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(partialTick, this.zo, this.z) - vec3.z());

        float scale = Mth.lerp(partialTick, this.scaleO, this.scale) * 0.35f;

        var texture = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(ChangedBlocks.WHITE_LATEX_BLOCK.get().defaultBlockState(),
                level,
                entity.blockPosition());

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-0.5F, -0.5F, -0.5F),
                new Vector3f(-0.5F, 0.5F, -0.5F),
                new Vector3f(0.5F, 0.5F, -0.5F),
                new Vector3f(0.5F, -0.5F, -0.5F),

                new Vector3f(0.5F, -0.5F, 0.5F),
                new Vector3f(0.5F, 0.5F, 0.5F),
                new Vector3f(-0.5F, 0.5F, 0.5F),
                new Vector3f(-0.5F, -0.5F, 0.5F),

                new Vector3f(-0.5F, -0.5F, 0.5F),
                new Vector3f(-0.5F, 0.5F, 0.5F),
                new Vector3f(-0.5F, 0.5F, -0.5F),
                new Vector3f(-0.5F, -0.5F, -0.5F),

                new Vector3f(0.5F, -0.5F, -0.5F),
                new Vector3f(0.5F, 0.5F, -0.5F),
                new Vector3f(0.5F, 0.5F, 0.5F),
                new Vector3f(0.5F, -0.5F, 0.5F),

                new Vector3f(-0.5F, 0.5F, 0.5F),
                new Vector3f(0.5F, 0.5F, 0.5F),
                new Vector3f(0.5F, 0.5F, -0.5F),
                new Vector3f(-0.5F, 0.5F, -0.5F),

                new Vector3f(-0.5F, -0.5F, -0.5F),
                new Vector3f(0.5F, -0.5F, -0.5F),
                new Vector3f(0.5F, -0.5F, 0.5F),
                new Vector3f(-0.5F, -0.5F, 0.5F)
        };
        Vector3f[] normals = new Vector3f[] {
                new Vector3f(0.0F, 0.0F, -1.0F),
                new Vector3f(0.0F, 0.0F, 1.0F),
                new Vector3f(-1.0F, 0.0F, 0.0F),
                new Vector3f(1.0F, 0.0F, 0.0F),
                new Vector3f(0.0F, 1.0F, 0.0F),
                new Vector3f(0.0F, -1.0F, 0.0F),
        };

        for (int i = 0; i < vertices.length; ++i) {
            Vector3f vector3f = vertices[i];
            vector3f.rotate(this.rotation);
            vector3f.mul(scale);
            vector3f.add(x, y, z);
        }

        for (int i = 0; i < normals.length; ++i) {
            Vector3f vector3f = normals[i];
            vector3f.rotate(this.rotation);
        }

        float u0 = texture.getU0();
        float u1 = texture.getU1();
        float v0 = texture.getV0();
        float v1 = texture.getV1();
        int j = this.getLightColor(partialTick);
        for (int i = 0; i < vertices.length/4; ++i) {
            int quadStart = i * 4;
            var normal = normals[i];
            buffer.vertex(vertices[0 + quadStart].x(), vertices[0 + quadStart].y(), vertices[0 + quadStart].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(u1, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(normal.x(), normal.y(), normal.z()).endVertex();
            buffer.vertex(vertices[1 + quadStart].x(), vertices[1 + quadStart].y(), vertices[1 + quadStart].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(u1, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(normal.x(), normal.y(), normal.z()).endVertex();
            buffer.vertex(vertices[2 + quadStart].x(), vertices[2 + quadStart].y(), vertices[2 + quadStart].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(u0, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(normal.x(), normal.y(), normal.z()).endVertex();
            buffer.vertex(vertices[3 + quadStart].x(), vertices[3 + quadStart].y(), vertices[3 + quadStart].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv(u0, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j).normal(normal.x(), normal.y(), normal.z()).endVertex();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return LatexParticleRenderType.TERRAIN_SHEET_3D_OPAQUE;
    }
}