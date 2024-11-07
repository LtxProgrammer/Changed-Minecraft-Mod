package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class WaveVisionRenderer {
    private final LevelRenderer levelRenderer;
    private final Minecraft minecraft;
    private final ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private float waveEffect = 0.0f;

    public static final Vector3f LATEX_RESONANCE_NEUTRAL = new Vector3f(1.0f, 1.0f, 1.0f);

    public WaveVisionRenderer(LevelRenderer levelRenderer, Minecraft minecraft, ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum, EntityRenderDispatcher entityRenderDispatcher) {
        this.levelRenderer = levelRenderer;
        this.minecraft = minecraft;
        this.renderChunksInFrustum = renderChunksInFrustum;
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    public void prepare(float waveEffect) {
        this.waveEffect = waveEffect;
    }

    public void renderAndSetupFog(ProfilerFiller profiler) {
        profiler.popPush("clear");
        RenderSystem.setShaderFogColor(0.0f, 0.0f, 0.0f);
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        profiler.popPush("fog");
        RenderSystem.setShaderFogStart(0.0f);
        RenderSystem.setShaderFogEnd(24.0f);
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }

    private void renderChunkLayer(RenderType renderType, RenderType actualRenderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        RenderSystem.assertOnRenderThread();
        actualRenderType.setupRenderState();

        this.minecraft.getProfiler().push("filterempty");
        this.minecraft.getProfiler().popPush(() -> {
            return "render_" + renderType;
        });
        ObjectListIterator<LevelRenderer.RenderChunkInfo> chunkInfoIterator = this.renderChunksInFrustum.listIterator();
        VertexFormat vertexformat = actualRenderType.format();
        ShaderInstance shader = RenderSystem.getShader();
        BufferUploader.reset();

        for(int k = 0; k < 12; ++k) {
            int i = RenderSystem.getShaderTexture(k);
            shader.setSampler("Sampler" + k, i);
        }

        if (shader.MODEL_VIEW_MATRIX != null) {
            shader.MODEL_VIEW_MATRIX.set(poseStack.last().pose());
        }

        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(projectionMatrix);
        }

        if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }

        if (shader.FOG_START != null) {
            shader.FOG_START.set(RenderSystem.getShaderFogStart());
        }

        if (shader.FOG_END != null) {
            shader.FOG_END.set(RenderSystem.getShaderFogEnd());
        }

        if (shader.FOG_COLOR != null) {
            shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }

        if (shader.FOG_SHAPE != null) {
            shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }

        if (shader.TEXTURE_MATRIX != null) {
            shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }

        if (shader.GAME_TIME != null) {
            shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        RenderSystem.setupShaderLights(shader);
        shader.apply();
        Uniform uniform = shader.CHUNK_OFFSET;
        boolean flag1 = false;

        while (chunkInfoIterator.hasNext()) {
            LevelRenderer.RenderChunkInfo chunkInfo = chunkInfoIterator.next();
            ChunkRenderDispatcher.RenderChunk renderChunk = chunkInfo.chunk;
            if (!renderChunk.getCompiledChunk().isEmpty(renderType)) {
                VertexBuffer vertexbuffer = renderChunk.getBuffer(renderType);
                BlockPos blockpos = renderChunk.getOrigin();
                if (uniform != null) {
                    uniform.set((float) ((double) blockpos.getX() - camX), (float) ((double) blockpos.getY() - camY), (float) ((double) blockpos.getZ() - camZ));
                    uniform.upload();
                }

                vertexbuffer.drawChunkLayer();
                flag1 = true;
            }
        }

        if (uniform != null) {
            uniform.set(Vector3f.ZERO);
        }

        shader.clear();
        if (flag1) {
            vertexformat.clearBufferState();
        }

        VertexBuffer.unbind();
        VertexBuffer.unbindVertexArray();
        this.minecraft.getProfiler().pop();
        actualRenderType.clearRenderState();
    }

    public void renderTerrain(ProfilerFiller profiler, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        ChangedClient.resetWaveResonance();
        this.renderChunkLayer(RenderType.solid(), ChangedShaders.waveVisionSolid(), poseStack, camX, camY, camZ, projectionMatrix);
        ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);
        this.renderChunkLayer(ChangedShaders.latexSolid(), ChangedShaders.latexWaveVisionSolid(), poseStack, camX, camY, camZ, projectionMatrix);

        ChangedClient.resetWaveResonance();
        this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, this.minecraft.options.mipmapLevels > 0);
        this.renderChunkLayer(RenderType.cutoutMipped(), ChangedShaders.waveVisionCutoutMipped(), poseStack, camX, camY, camZ, projectionMatrix);
        //ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);
        //this.renderChunkLayer(RenderType.cutoutMipped(), ChangedShaders.latexWaveVisionCutoutMipped(), poseStack, camX, camY, camZ, projectionMatrix);

        ChangedClient.resetWaveResonance();
        this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).restoreLastBlurMipmap();
        this.renderChunkLayer(RenderType.cutout(), ChangedShaders.waveVisionCutout(), poseStack, camX, camY, camZ, projectionMatrix);
        //ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);
        //this.renderChunkLayer(RenderType.cutout(), ChangedShaders.latexWaveVisionCutout(), poseStack, camX, camY, camZ, projectionMatrix);

        ChangedClient.resetWaveResonance();
    }

    public void renderBlockEntity() {

    }

    public void renderEntity(Entity entity, double camX, double camY, double camZ, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource) {
        double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
        float bodyRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        this.entityRenderDispatcher.render(entity, x - camX, y - camY, z - camZ, bodyRot, partialTicks, poseStack, bufferSource, this.entityRenderDispatcher.getPackedLightCoords(entity, partialTicks));
    }

    public int renderEntities(ClientLevel level, Frustum frustum, Camera camera, PoseStack poseStack, double camX, double camY, double camZ, float partialTicks, MultiBufferSource bufferSource, Runnable submitDrawCall) {
        int totalEntities = 0;

        ChangedClient.setWaveResonance(LATEX_RESONANCE_NEUTRAL);

        for (Entity entity : level.entitiesForRendering()) {
            if (this.entityRenderDispatcher.shouldRender(entity, frustum, camX, camY, camZ) || entity.hasIndirectPassenger(this.minecraft.player)) {
                BlockPos blockpos = entity.blockPosition();
                if ((level.isOutsideBuildHeight(blockpos.getY()) || levelRenderer.isChunkCompiled(blockpos)) && (entity != camera.getEntity() || camera.isDetached() || camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).isSleeping()) && (!(entity instanceof LocalPlayer) || camera.getEntity() == entity || (entity == minecraft.player && !minecraft.player.isSpectator()))) { //FORGE: render local player entity when it is not the renderViewEntity
                    ++totalEntities;
                    if (entity.tickCount == 0) {
                        entity.xOld = entity.getX();
                        entity.yOld = entity.getY();
                        entity.zOld = entity.getZ();
                    }

                    this.renderEntity(entity, camX, camY, camZ, partialTicks, poseStack, bufferSource);
                }
            }
        }

        submitDrawCall.run();

        ChangedClient.resetWaveResonance();

        return totalEntities;
    }
}
