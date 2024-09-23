package net.ltxprogrammer.changed.client.latexparticles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.ltxprogrammer.changed.client.ModelPartStem;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

    private final ChangedEntity attachedEntity;
    private final ModelPartStem attachedPart;
    private final AdvancedHumanoidModel<?> attachedModel;
  
    private final SurfacePoint surface;
    protected final Color3 color;
    protected final float alpha;
    private boolean attached = true;
    private boolean wasPreppedWhenDetached = false;
    private boolean preppedForRender = false;
    private boolean preppedForTick = false;
    private SetupContext setupContext = SetupContext.THIRD_PERSON;

    public boolean shouldRender() {
        return attached ?
                this.preppedForRender :
                this.wasPreppedWhenDetached;
    }

    public LatexDripParticle(SpriteSet spriteSet,
                             ChangedEntity attachedEntity, AdvancedHumanoidModel<?> attachedModel, ModelPartStem attachedPart, SurfacePoint surface, Color3 color, float alpha, int lifespan) {
        super(attachedEntity.level, lifespan);
        this.maxTicksAttached = attachedEntity.level.random.nextInt(80, 2400);

        this.spriteSet = spriteSet;
        this.attachedEntity = attachedEntity;
        this.attachedModel = attachedModel;
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
        if (this.attached) {
            this.attached = false;
            this.wasPreppedWhenDetached = this.preppedForTick;
        }
    }

    @Override
    public void tick() {
        if (!attached)
            super.tick();

        if (attached) {
            ticksAttached++;
            this.xd = this.x - this.xo;
            this.yd = this.y - this.yo;
            this.zd = this.z - this.zo;
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
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

        if (attached && ticksAttached > maxTicksAttached)
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

        this.preppedForTick = false;
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

            boolean lastOnGround = this.isOnGround;
            boolean lastOnWall = this.isOnWall;
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

            if ((!lastOnWall && this.isOnWall) || (!lastOnGround && this.isOnGround))
                onCollide();
        }
    }

    @Override
    public void onCollide() {
        if (!shouldRender())
            return; // No sound
        Player localPlayer = UniversalDist.getLocalPlayer();
        level.playLocalSound(x, y, z, ChangedSounds.LATEX_DRIP, ProcessTransfur.isPlayerTransfurred(localPlayer) ? SoundSource.NEUTRAL : SoundSource.HOSTILE, 0.025f, 1.0f, true);
    }

    @Override
    public void setupForRender(PoseStack poseStack, float partialTick, SetupContext setupContext) {
        super.setupForRender(poseStack, partialTick, setupContext);
        if (!attached)
            return;
        if (!attachedPart.stem.stream().allMatch(part -> part.visible))
            return;

        poseStack.pushPose();
        if (this.attachedModel instanceof AdvancedHumanoidModelInterface<?,?> modelInterface) {
            if (this.attachedPart.getRoot() == attachedModel.getHead())
                modelInterface.scaleForHead(poseStack);
            else
                modelInterface.scaleForBody(poseStack);
        }

        attachedPart.translateAndRotate(poseStack);
        // in C = A * B, this is C
        var modelSpaceToScreenSpace = poseStack.last().pose();
        var modelSpaceToScreenSpaceN = poseStack.last().normal();
        poseStack.popPose();

        Vector4f translationVector = new Vector4f(surface.position().x(), surface.position().y(), surface.position().z(), 1.0f);
        translationVector.transform(modelSpaceToScreenSpace); // Coordinates now in screenspace

        Vector4f worldSpace = CameraUtil.toWorldSpace(translationVector);
        x = worldSpace.x();
        y = worldSpace.y();
        z = worldSpace.z();

        surfaceNormalRelativeCamera.load(surface.normal());
        surfaceNormalRelativeCamera.transform(modelSpaceToScreenSpaceN);

        this.setupContext = setupContext;
        this.preppedForRender = true;
        this.preppedForTick = true;
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
    public void renderFromLayer(MultiBufferSource buffer, float partialTicks) {

    }

    @Override
    public void renderFromEvent(VertexConsumer buffer, Camera camera, float partialTicks, SetupContext context) {
        boolean isCamEntity = camera.getEntity() == this.attachedEntity;
        if (!isCamEntity && this.attachedEntity.getUnderlyingPlayer() != null) {
            isCamEntity = camera.getEntity() == this.attachedEntity.getUnderlyingPlayer();
        }

        if (isCamEntity && attached) {
            if (ChangedCompatibility.isFirstPersonRendering()) {
                var root = this.attachedPart.getRoot();
                if (this.attachedModel.getHead() == root)
                    return;
                if (this.attachedEntity.isVisuallySwimming() && this.attachedModel.getTorso() == root)
                    return;
            }

            else if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                if (this.setupContext != SetupContext.FIRST_PERSON || context != SetupContext.FIRST_PERSON)
                    return;
            }
        }

        else if (!isCamEntity && context == SetupContext.FIRST_PERSON)
            return;

        this.render(buffer, camera, partialTicks);
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (!shouldRender())
            return; // No render
        preppedForRender = false;

        Vec3 vec3 = camera.getPosition();
        float lerpX, lerpY, lerpZ;
        if (attached) { // Position is updated every frame, so no lerping required
            lerpX = (float)(this.x - vec3.x());
            lerpY = (float)(this.y - vec3.y());
            lerpZ = (float)(this.z - vec3.z());
        } else {
            lerpX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
            lerpY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
            lerpZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        }
        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(camera.rotation());
        float quadSize = this.getSpriteSize(partialTicks);

        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();

        Vector3f[] genVec = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = genVec[i];
            vector3f.transform(camera.rotation());
            vector3f.mul(quadSize);
            vector3f.add(lerpX, lerpY, lerpZ);
        }

        int lightColor = this.getLightColor(partialTicks);
        int overlay = attached ? LivingEntityRenderer.getOverlayCoords(attachedEntity, 0.0F) : OverlayTexture.NO_OVERLAY;
        float normX = attached ? surfaceNormalRelativeCamera.x() : 0;
        float normY = attached ? surfaceNormalRelativeCamera.y() : 0;
        float normZ = attached ? surfaceNormalRelativeCamera.z() : 1;

        buffer.vertex(genVec[0].x(), genVec[0].y(), genVec[0].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u1, v1)
                .overlayCoords(overlay).uv2(lightColor).normal(normX, normY, normZ).endVertex();
        buffer.vertex(genVec[1].x(), genVec[1].y(), genVec[1].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u1, v0)
                .overlayCoords(overlay).uv2(lightColor).normal(normX, normY, normZ).endVertex();
        buffer.vertex(genVec[2].x(), genVec[2].y(), genVec[2].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u0, v0)
                .overlayCoords(overlay).uv2(lightColor).normal(normX, normY, normZ).endVertex();
        buffer.vertex(genVec[3].x(), genVec[3].y(), genVec[3].z()).color(this.color.red(), this.color.green(), this.color.blue(), this.alpha).uv(u0, v1)
                .overlayCoords(overlay).uv2(lightColor).normal(normX, normY, normZ).endVertex();
    }

    @Override
    public boolean shouldExpire() {
        return super.shouldExpire() || attachedEntity == null || attachedEntity.isDeadOrDying() || attachedEntity.isRemoved() || attachedEntity.isInvisible();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return alpha >= 1.0f ? LatexParticleRenderType.LATEX_PARTICLE_SHEET_3D_OPAQUE : LatexParticleRenderType.LATEX_PARTICLE_SHEET_3D_TRANSLUCENT;
    }

    public static LatexParticleProvider<LatexDripParticle> of(ChangedEntity attachedEntity, AdvancedHumanoidModel<?> attachedModel, ModelPartStem attachedPart, SurfacePoint surface, Color3 color, float alpha, int lifespan) {
        return new LatexParticleProvider<>() {
            @Override
            public LatexParticleType<LatexDripParticle> getParticleType() {
                return LatexParticleType.LATEX_DRIP_PARTICLE.get();
            }

            @Override
            public LatexDripParticle create(SpriteSet spriteSet) {
                return new LatexDripParticle(spriteSet, attachedEntity, attachedModel, attachedPart, surface, color, alpha, lifespan);
            }
        };
    }
}
