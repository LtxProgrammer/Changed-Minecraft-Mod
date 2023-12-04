package net.ltxprogrammer.changed.client.latexparticles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.ltxprogrammer.changed.client.ModelPartStem;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LatexDripParticle extends LatexParticle {
    private final SpriteSet spriteSet;
    protected TextureAtlasSprite sprite;

    protected double gravity, friction;
    protected Vector3f surfaceNormalRelativeCamera = new Vector3f(0, 0, 1);
    protected Vector3f surfaceTangentRelativeCamera = new Vector3f(0, -1, 0);
    protected double x, xo;
    protected double y, yo;
    protected double z, zo;
    protected double xd = 0.0;
    protected double yd = 0.0;
    protected double zd = 0.0;
    protected Vec3 velocity = Vec3.ZERO;
    protected int ticksAttached = 0;
    private final int maxTicksAttached;

    private final LatexEntity attachedEntity;
    private final ModelPartStem attachedPart;
  
    private final SurfacePoint surface;
    protected final Color3 color;
    protected final float alpha;
    private boolean attached = true;
    private boolean prepped = false;

    public LatexDripParticle(SpriteSet spriteSet,
                             LatexEntity attachedEntity, ModelPartStem attachedPart, SurfacePoint surface, Color3 color, float alpha, int lifespan) {
        super(attachedEntity.level, lifespan);
        this.maxTicksAttached = attachedEntity.level.random.nextInt(80, 2400);

        this.spriteSet = spriteSet;
        this.attachedEntity = attachedEntity;
        this.attachedPart = attachedPart;
        this.surface = surface;
        this.color = color;
        this.alpha = alpha;

        this.gravity = 0.65f;
        this.friction = 0.98f;
        this.sprite = spriteSet.get(0, 3);
    }

    public float getSpriteSize(float partialTicks) {
        return ((float)ticksAttached / (float)maxTicksAttached) * 0.075f + 0.05f;
    }

    public void detach() {
        this.attached = false;
    }

    @Override
    public void tick() {
        if (!attached)
            super.tick();

        if (attached) {
            ticksAttached++;
            if (maxTicksAttached - ticksAttached < 60)
                this.sprite = spriteSet.get(1, 3);
            else
                this.sprite = spriteSet.get(0, 3);
        } else if (isOnGround) {
            this.sprite = spriteSet.get(3, 3);
        } else if (isOnWall) {
            this.sprite = spriteSet.get(1, 3);
        } else {
            this.sprite = spriteSet.get(2, 3);
        }

        if (ticksAttached > maxTicksAttached)
            detach();
        
        if (!attached) { // Gravity
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            
            this.yd -= 0.04D * (double)this.gravity;
            this.move(xd, yd, zd);

            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            if (this.isOnWall) {
                this.xd *= 0.7F;
                this.yd *= 0.5F;
                this.zd *= 0.7F;
            }
            
            if (this.isOnGround) {
                this.xd *= 0.7F;
                this.zd *= 0.7F;
            }
        }
    }
    
    public void move(double xd, double yd, double zd) {
        if (!this.stoppedByCollision) {
            double presimXd = xd;
            double presimYd = yd;
            double presimZd = zd;
            if (/*this.hasPhysics &&*/ (xd != 0.0D || yd != 0.0D || zd != 0.0D) && xd * xd + yd * yd + zd * zd < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(xd, yd, zd), this.getBoundingBox(), this.level, List.of());
                xd = vec3.x;
                yd = vec3.y;
                zd = vec3.z;
            }

            if (xd != 0.0D || yd != 0.0D || zd != 0.0D) {
                //this.setBoundingBox(this.getBoundingBox().move(xd, yd, zd));
                this.x += xd;
                this.y += yd;
                this.z += zd;
            }

            if (Math.abs(presimYd) >= (double)1.0E-5F && Math.abs(yd) < (double)1.0E-5F) {
                this.stoppedByCollision = true;
            }

            this.isOnGround = presimYd != yd && presimYd < 0.0D;
            if (presimXd != xd) {
                this.xd = 0.0D;
            }

            if (presimZd != zd) {
                this.zd = 0.0D;
            }

            if (!this.isOnWall)
                this.isOnWall = (presimXd != xd && Math.abs(presimXd) > 1.0E-5F && xd == 0.0D) ||
                    (presimZd != zd && Math.abs(presimZd) > 1.0E-5F && zd == 0.0D);
        }
    }

    @Override
    public void setupForRender(PoseStack poseStack, float partialTicks) {
        super.setupForRender(poseStack, partialTicks);
        if (!attached)
            return;

        PoseStackExtender poseStackExtender = (PoseStackExtender)poseStack;
        Vec3 entityPosition = attachedEntity.getPosition(partialTicks);
        // TODO maybe deprecate? or hold onto in the case of inventory rendering
        /*Vec3 cameraOffset = EntityRenderHelper.ENTITY_RENDER_DISPATCHER_ENTITY_MINUS_CAMERA.getLast()
                .subtract(entityPosition)
                .multiply(-1, -1, -1);*/

        poseStack.pushPose();
        if (attachedEntity.getUnderlyingPlayer() != null && attachedEntity.isCrouching())
            poseStack.translate(0.0, 0.125, 0.0); // This is to match the offset in the PlayerRenderer. TODO maybe mixin remove offset if player is latex?

        attachedPart.translateAndRotate(poseStack);
        // in C = A * B, this is C
        var modelSpaceToScreenSpace = poseStack.last().pose();
        var modelSpaceToScreenSpaceN = poseStack.last().normal();
        poseStack.popPose();

        var modelSpaceToWorldSpace = poseStackExtender.popAndRepush(pose -> {
            // in C = A * B, this is A
            var worldSpaceToModelSpace = pose.pose();
            worldSpaceToModelSpace.invert();
            worldSpaceToModelSpace.multiply(modelSpaceToScreenSpace);
            worldSpaceToModelSpace.translate(new Vector3f((float)entityPosition.x, (float)entityPosition.y, (float)entityPosition.z));
            return worldSpaceToModelSpace;
        });

        Vector4f translationVector = new Vector4f(surface.position().x(), surface.position().y(), surface.position().z(), 1.0f);
        translationVector.transform(modelSpaceToWorldSpace);
        xd = translationVector.x() - x;
        yd = translationVector.y() - y;
        zd = translationVector.z() - z;
        
        x = translationVector.x();
        y = translationVector.y();
        z = translationVector.z();

        Vector3f normalVector = new Vector3f(surface.normal().x(), surface.normal().y(), surface.normal().z());
        normalVector.transform(modelSpaceToScreenSpaceN);

        surfaceNormalRelativeCamera.load(surface.normal());
        surfaceNormalRelativeCamera.transform(modelSpaceToScreenSpaceN);

        xo = x;
        yo = y;
        zo = z;

        prepped = true;
    }

    @Override
    public boolean isForEntity(Entity entity) {
        return attached && attachedEntity == entity;
    }

    protected int getLightColor(float partialTicks) {
        BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
        return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }

    @Override
    public AABB getBoundingBox() {
        float spriteSize = getSpriteSize(0.0f);
        return AABB.ofSize(new Vec3(x, y, z), spriteSize, spriteSize, spriteSize);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (attached && !prepped)
            return; // No render
        prepped = false;

        Vec3 vec3 = camera.getPosition();
        float lerpX = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float lerpY = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float lerpZ = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(camera.rotation());
        float quadSize = this.getSpriteSize(partialTicks);

        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();
        int lightColor = this.getLightColor(partialTicks);
        int overlay = LivingEntityRenderer.getOverlayCoords(attachedEntity, 0.0F);

        if (attached) {
            // TODO align quad to normal and tangent
            Vector3f[] genVec = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

            for(int i = 0; i < 4; ++i) {
                Vector3f vector3f = genVec[i];
                vector3f.transform(camera.rotation());
                vector3f.mul(quadSize);
                vector3f.add(lerpX, lerpY, lerpZ);
            }

            buffer.vertex(genVec[0].x(), genVec[0].y(), genVec[0].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u1, v1)
                    .overlayCoords(overlay).uv2(lightColor).normal(surfaceNormalRelativeCamera.x(), surfaceNormalRelativeCamera.y(), surfaceNormalRelativeCamera.z()).endVertex();
            buffer.vertex(genVec[1].x(), genVec[1].y(), genVec[1].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u1, v0)
                    .overlayCoords(overlay).uv2(lightColor).normal(surfaceNormalRelativeCamera.x(), surfaceNormalRelativeCamera.y(), surfaceNormalRelativeCamera.z()).endVertex();
            buffer.vertex(genVec[2].x(), genVec[2].y(), genVec[2].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u0, v0)
                    .overlayCoords(overlay).uv2(lightColor).normal(surfaceNormalRelativeCamera.x(), surfaceNormalRelativeCamera.y(), surfaceNormalRelativeCamera.z()).endVertex();
            buffer.vertex(genVec[3].x(), genVec[3].y(), genVec[3].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u0, v1)
                    .overlayCoords(overlay).uv2(lightColor).normal(surfaceNormalRelativeCamera.x(), surfaceNormalRelativeCamera.y(), surfaceNormalRelativeCamera.z()).endVertex();
        } else {
            Vector3f[] genVec = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

            for(int i = 0; i < 4; ++i) {
                Vector3f vector3f = genVec[i];
                vector3f.transform(camera.rotation());
                vector3f.mul(quadSize);
                vector3f.add(lerpX, lerpY, lerpZ);
            }

            buffer.vertex(genVec[0].x(), genVec[0].y(), genVec[0].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u1, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightColor).normal(0, 0, 1).endVertex();
            buffer.vertex(genVec[1].x(), genVec[1].y(), genVec[1].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u1, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightColor).normal(0, 0, 1).endVertex();
            buffer.vertex(genVec[2].x(), genVec[2].y(), genVec[2].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u0, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightColor).normal(0, 0, 1).endVertex();
            buffer.vertex(genVec[3].x(), genVec[3].y(), genVec[3].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u0, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightColor).normal(0, 0, 1).endVertex();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return LatexParticleRenderType.LATEX_PARTICLE_SHEET_3D_OPAQUE;
    }

    public static LatexParticleProvider<LatexDripParticle> of(LatexEntity attachedEntity, ModelPartStem attachedPart, SurfacePoint surface, Color3 color, float alpha, int lifespan) {
        return new LatexParticleProvider<>() {
            @Override
            public LatexParticleType<LatexDripParticle> getParticleType() {
                return LatexParticleType.LATEX_DRIP_PARTICLE.get();
            }

            @Override
            public LatexDripParticle create(SpriteSet spriteSet) {
                return new LatexDripParticle(spriteSet, attachedEntity, attachedPart, surface, color, alpha, lifespan);
            }
        };
    }
}
