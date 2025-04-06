package net.ltxprogrammer.changed.mixin.render;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.client.*;
import net.ltxprogrammer.changed.item.LoopedRecordItem;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow protected abstract void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_);
    @Shadow @Final private Map<BlockPos, SoundInstance> playingRecords;
    @Shadow @Final private Minecraft minecraft;
    @Shadow protected abstract void notifyNearbyEntities(Level p_109551_, BlockPos p_109552_, boolean p_109553_);
    @Shadow @Nullable private ClientLevel level;

    @Shadow public abstract void renderSky(PoseStack p_202424_, Matrix4f p_202425_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_);

    @Shadow protected abstract void compileChunks(Camera p_194371_);

    @Shadow private boolean captureFrustum;

    @Shadow protected abstract void captureFrustum(Matrix4f p_109526_, Matrix4f p_109527_, double p_109528_, double p_109529_, double p_109530_, Frustum p_109531_);

    @Shadow private Frustum cullingFrustum;

    @Shadow @Nullable private Frustum capturedFrustum;

    @Shadow @Final private Vector3d frustumPos;

    @Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow private int renderedEntities;

    @Shadow private int culledEntities;

    @Shadow @Nullable private RenderTarget itemEntityTarget;

    @Shadow @Nullable private RenderTarget weatherTarget;

    @Shadow protected abstract boolean shouldShowEntityOutlines();

    @Shadow @Nullable private RenderTarget entityTarget;

    @Shadow @Final private RenderBuffers renderBuffers;

    @Shadow public abstract boolean isChunkCompiled(BlockPos p_202431_);

    @Shadow protected abstract void renderEntity(Entity p_109518_, double p_109519_, double p_109520_, double p_109521_, float p_109522_, PoseStack p_109523_, MultiBufferSource p_109524_);

    @Shadow protected abstract void checkPoseStack(PoseStack p_109589_);

    @Shadow @Final private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;
    @Shadow @Final private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;
    @Shadow @Final private Set<BlockEntity> globalBlockEntities;
    @Shadow @Nullable private PostChain entityEffect;

    @Shadow protected abstract void renderHitOutline(PoseStack p_109638_, VertexConsumer p_109639_, Entity p_109640_, double p_109641_, double p_109642_, double p_109643_, BlockPos p_109644_, BlockState p_109645_);
    @Shadow public abstract void renderClouds(PoseStack p_172955_, Matrix4f p_172956_, float p_172957_, double p_172958_, double p_172959_, double p_172960_);
    @Shadow protected abstract void renderSnowAndRain(LightTexture p_109704_, float p_109705_, double p_109706_, double p_109707_, double p_109708_);
    @Shadow protected abstract void renderWorldBorder(Camera p_173013_);
    @Shadow protected abstract void renderDebug(Camera p_109794_);

    @Shadow private int lastViewDistance;

    @Shadow private int lastCameraChunkX;
    @Shadow private int lastCameraChunkY;
    @Shadow private int lastCameraChunkZ;
    @Shadow private double lastCameraX;
    @Shadow private double lastCameraY;
    @Shadow private double lastCameraZ;
    @Shadow @Nullable private ChunkRenderDispatcher chunkRenderDispatcher;
    @Shadow private boolean needsFullRenderChunkUpdate;
    @Shadow @Nullable private ViewArea viewArea;
    @Shadow @Final private AtomicLong nextFullUpdateMillis;
    @Shadow private double prevCamX;
    @Shadow private double prevCamY;
    @Shadow private double prevCamZ;
    @Shadow @Nullable private Future<?> lastFullRenderChunkUpdate;

    @Shadow protected abstract void initializeQueueForFullUpdate(Camera p_194344_, Queue<LevelRenderer.RenderChunkInfo> p_194345_);
    @Shadow protected abstract void updateRenderChunks(LinkedHashSet<LevelRenderer.RenderChunkInfo> p_194363_, LevelRenderer.RenderInfoMap p_194364_, Vec3 p_194365_, Queue<LevelRenderer.RenderChunkInfo> p_194366_, boolean p_194367_);
    @Shadow protected abstract void applyFrustum(Frustum p_194355_);
    @Shadow public abstract void graphicsChanged();

    @Shadow @Final private AtomicReference<LevelRenderer.RenderChunkStorage> renderChunkStorage;
    @Shadow @Final private AtomicBoolean needsFrustumUpdate;
    @Shadow @Final private BlockingQueue<ChunkRenderDispatcher.RenderChunk> recentlyCompiledChunks;

    @Shadow private double prevCamRotX;
    @Shadow private double prevCamRotY;
    @Shadow private boolean generateClouds;


    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    public void postRenderLayer(RenderType type, PoseStack pose, double x, double y, double z, Matrix4f matrix, CallbackInfo callback) {
        if (type == RenderType.solid()) {
            LatexCoveredBlockRenderer.isRenderingChangedBlockLayer = true;
            renderChunkLayer(ChangedShaders.latexSolid(), pose, x, y, z, matrix);
            LatexCoveredBlockRenderer.isRenderingChangedBlockLayer = false;
        }
        else if (type == RenderType.cutoutMipped()) {
            LatexCoveredBlockRenderer.isRenderingChangedBlockLayer = true;
            renderChunkLayer(ChangedShaders.latexCutoutMipped(), pose, x, y, z, matrix);
            LatexCoveredBlockRenderer.isRenderingChangedBlockLayer = false;
        }
        else if (type == RenderType.cutout()) {
            LatexCoveredBlockRenderer.isRenderingChangedBlockLayer = true;
            renderChunkLayer(ChangedShaders.latexCutout(), pose, x, y, z, matrix);
            LatexCoveredBlockRenderer.isRenderingChangedBlockLayer = false;
        }
    }

    @Inject(method = "playStreamingMusic(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/RecordItem;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forRecord(Lnet/minecraft/sounds/SoundEvent;DDD)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"), cancellable = true)
    public void orPlayLoopedTrack(SoundEvent event, BlockPos pos, RecordItem musicDiscItem, CallbackInfo callback) {
        if (musicDiscItem instanceof LoopedRecordItem) {
            callback.cancel();

            SoundInstance simplesoundinstance = new SimpleSoundInstance(event.getLocation(), SoundSource.RECORDS, 4.0F, 1.0F, true, 0, SoundInstance.Attenuation.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
            this.playingRecords.put(pos, simplesoundinstance);
            this.minecraft.getSoundManager().play(simplesoundinstance);

            this.notifyNearbyEntities(this.level, pos, event != null);
        }
    }

    @Inject(method = "prepareCullFrustum", at = @At("TAIL"))
    public void captureInverseMatrix(PoseStack poseStack, Vec3 cameraPosition, Matrix4f projectionMatrix, CallbackInfo ci) {
        Matrix4f modelViewCopy = poseStack.last().pose().copy();

        modelViewCopy.invert();
        modelViewCopy.translate(new Vector3f((float) cameraPosition.x, (float) cameraPosition.y, (float) cameraPosition.z));

        CameraUtil.setInverseMatrix(modelViewCopy);
    }

    @Unique private final Cacheable<WaveVisionRenderer> waveVisionRendererCache = Cacheable.of(() -> new WaveVisionRenderer(
            (LevelRenderer)(Object)this,
            this.minecraft,
            this.renderChunksInFrustum,
            this.entityRenderDispatcher
    ));

    @Inject(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Z)V", at = @At("HEAD"))
    public void ensureChunkIsUpdated(BlockPos blockPos, boolean important, CallbackInfo ci) {
        if (!ChangedClient.isRenderingWaveVision()) return; // Sodium overwrites this function, so we have to put it back

        for(int z = blockPos.getZ() - 1; z <= blockPos.getZ() + 1; ++z) {
            for(int x = blockPos.getX() - 1; x <= blockPos.getX() + 1; ++x) {
                for(int y = blockPos.getY() - 1; y <= blockPos.getY() + 1; ++y) {
                    this.viewArea.setDirty(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(y), SectionPos.blockToSectionCoord(z), important);
                }
            }
        }
    }

    @Inject(method = "allChanged", at = @At("HEAD"), cancellable = true)
    public void overrideAllChangedIfWaveVision(CallbackInfo ci) {
        if (ChangedClient.shouldRenderingWaveVision()) {
            ci.cancel();
            waveVisionAllChanged();
        }
    }

    @Unique
    public void waveVisionAllChanged() {
        if (this.level != null) {
            this.graphicsChanged();
            this.level.clearTintCaches();
            if (this.chunkRenderDispatcher == null) {
                this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.level, (LevelRenderer)(Object)this, Util.backgroundExecutor(), this.minecraft.is64Bit(), this.renderBuffers.fixedBufferPack());
            } else {
                this.chunkRenderDispatcher.setLevel(this.level);
            }

            this.needsFullRenderChunkUpdate = true;
            this.generateClouds = true;
            this.recentlyCompiledChunks.clear();
            ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
            this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
            if (this.viewArea != null) {
                this.viewArea.releaseAllBuffers();
            }

            this.chunkRenderDispatcher.blockUntilClear();
            synchronized(this.globalBlockEntities) {
                this.globalBlockEntities.clear();
            }

            this.viewArea = new ViewArea(this.chunkRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), (LevelRenderer)(Object)this);
            if (this.lastFullRenderChunkUpdate != null) {
                try {
                    this.lastFullRenderChunkUpdate.get();
                    this.lastFullRenderChunkUpdate = null;
                } catch (Exception exception) {
                    LOGGER.warn("Full update failed", (Throwable)exception);
                }
            }

            this.renderChunkStorage.set(new LevelRenderer.RenderChunkStorage(this.viewArea.chunks.length));
            this.renderChunksInFrustum.clear();
            Entity entity = this.minecraft.getCameraEntity();
            if (entity != null) {
                this.viewArea.repositionCamera(entity.getX(), entity.getZ());
            }
        }
    }

    @Unique
    private void waveVisionSetupRender(Camera camera, Frustum frustum, boolean frustumExists, boolean isSpectator) {
        Vec3 camPos = camera.getPosition();
        if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance)
            this.waveVisionAllChanged();

        this.level.getProfiler().push("camera");
        double playerX = this.minecraft.player.getX();
        double playerY = this.minecraft.player.getY();
        double playerZ = this.minecraft.player.getZ();
        int chunkX = SectionPos.posToSectionCoord(playerX);
        int chunkY = SectionPos.posToSectionCoord(playerY);
        int chunkZ = SectionPos.posToSectionCoord(playerZ);
        if (this.lastCameraChunkX != chunkX || this.lastCameraChunkY != chunkY || this.lastCameraChunkZ != chunkZ) {
            this.lastCameraX = playerX;
            this.lastCameraY = playerY;
            this.lastCameraZ = playerZ;
            this.lastCameraChunkX = chunkX;
            this.lastCameraChunkY = chunkY;
            this.lastCameraChunkZ = chunkZ;
            this.viewArea.repositionCamera(playerX, playerZ);
        }

        this.chunkRenderDispatcher.setCamera(camPos);
        this.level.getProfiler().popPush("cull");
        this.minecraft.getProfiler().popPush("culling");
        BlockPos blockpos = camera.getBlockPosition();
        double d3 = Math.floor(camPos.x / 8.0D);
        double d4 = Math.floor(camPos.y / 8.0D);
        double d5 = Math.floor(camPos.z / 8.0D);
        this.needsFullRenderChunkUpdate = this.needsFullRenderChunkUpdate || d3 != this.prevCamX || d4 != this.prevCamY || d5 != this.prevCamZ;
        this.nextFullUpdateMillis.updateAndGet((oldValue) -> {
            if (oldValue > 0L && System.currentTimeMillis() > oldValue) {
                this.needsFullRenderChunkUpdate = true;
                return 0L;
            } else {
                return oldValue;
            }
        });
        this.prevCamX = d3;
        this.prevCamY = d4;
        this.prevCamZ = d5;
        this.minecraft.getProfiler().popPush("update");
        boolean flag = this.minecraft.smartCull;
        if (isSpectator && this.level.getBlockState(blockpos).isSolidRender(this.level, blockpos)) {
            flag = false;
        }

        if (!frustumExists) {
            if (this.needsFullRenderChunkUpdate && (this.lastFullRenderChunkUpdate == null || this.lastFullRenderChunkUpdate.isDone())) {
                this.minecraft.getProfiler().push("full_update_schedule");
                this.needsFullRenderChunkUpdate = false;
                boolean flag1 = flag;
                this.lastFullRenderChunkUpdate = Util.backgroundExecutor().submit(() -> {
                    Queue<LevelRenderer.RenderChunkInfo> updateQueue = Queues.newArrayDeque();
                    this.initializeQueueForFullUpdate(camera, updateQueue);
                    LevelRenderer.RenderChunkStorage chunkStorage = new LevelRenderer.RenderChunkStorage(this.viewArea.chunks.length);
                    this.updateRenderChunks(chunkStorage.renderChunks, chunkStorage.renderInfoMap, camPos, updateQueue, flag1);
                    this.renderChunkStorage.set(chunkStorage);
                    this.needsFrustumUpdate.set(true);
                });
                this.minecraft.getProfiler().pop();
            }

            LevelRenderer.RenderChunkStorage levelrenderer$renderchunkstorage = this.renderChunkStorage.get();
            if (!this.recentlyCompiledChunks.isEmpty()) {
                this.minecraft.getProfiler().push("partial_update");
                Queue<LevelRenderer.RenderChunkInfo> updateQueue = Queues.newArrayDeque();

                while(!this.recentlyCompiledChunks.isEmpty()) {
                    ChunkRenderDispatcher.RenderChunk renderChunk = this.recentlyCompiledChunks.poll();
                    LevelRenderer.RenderChunkInfo renderChunkInfo = levelrenderer$renderchunkstorage.renderInfoMap.get(renderChunk);
                    if (renderChunkInfo != null && renderChunkInfo.chunk == renderChunk) {
                        updateQueue.add(renderChunkInfo);
                    }
                }

                this.updateRenderChunks(levelrenderer$renderchunkstorage.renderChunks, levelrenderer$renderchunkstorage.renderInfoMap, camPos, updateQueue, flag);
                this.needsFrustumUpdate.set(true);
                this.minecraft.getProfiler().pop();
            }

            double rotX = Math.floor((double)(camera.getXRot() / 2.0F));
            double rotY = Math.floor((double)(camera.getYRot() / 2.0F));
            if (this.needsFrustumUpdate.compareAndSet(true, false) || rotX != this.prevCamRotX || rotY != this.prevCamRotY) {
                this.applyFrustum((new Frustum(frustum)).offsetToFullyIncludeCameraCube(8));
                this.prevCamRotX = rotX;
                this.prevCamRotY = rotY;
            }
        }

        this.minecraft.getProfiler().pop();
    }

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    public void andRecordedTranslucent(RenderType renderType, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_, CallbackInfo ci) {
        if (renderType == RenderType.translucent())
            ChangedClient.runRecordedTranslucentRender(this.renderBuffers.bufferSource(), renderType);
    }

    @Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
    public void orRenderWaveVision(PoseStack poseStack, float partialTicks, long nanoseconds, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (!ChangedClient.shouldRenderingWaveVision())
            return;

        ci.cancel();

        ChangedClient.setRenderingWaveVision(true);

        var waveVisionRenderer = waveVisionRendererCache.get();
        waveVisionRenderer.prepare(ChangedClient.setupWaveVisionEffect(partialTicks));

        RenderSystem.setShaderGameTime(this.level.getGameTime(), partialTicks);
        this.blockEntityRenderDispatcher.prepare(this.level, camera, this.minecraft.hitResult);
        this.entityRenderDispatcher.prepare(this.level, camera, this.minecraft.crosshairPickEntity);
        ProfilerFiller profiler = level.getProfiler();
        profiler.popPush("light_update_queue");
        level.pollLightUpdates();
        profiler.popPush("light_updates");
        boolean flag = level.isLightUpdateQueueEmpty();
        level.getChunkSource().getLightEngine().runUpdates(Integer.MAX_VALUE, flag, true);
        Vec3 vec3 = camera.getPosition();
        double camX = vec3.x();
        double camY = vec3.y();
        double camZ = vec3.z();
        Matrix4f matrix4f = poseStack.last().pose();
        profiler.popPush("culling");
        boolean frustumExists = this.capturedFrustum != null;
        Frustum frustum;
        if (frustumExists) {
            frustum = this.capturedFrustum;
            frustum.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
        } else {
            frustum = this.cullingFrustum;
        }

        this.minecraft.getProfiler().popPush("captureFrustum");
        if (this.captureFrustum) {
            this.captureFrustum(matrix4f, projectionMatrix, vec3.x, vec3.y, vec3.z, frustumExists ? new Frustum(matrix4f, projectionMatrix) : frustum);
            this.captureFrustum = false;
        }

        waveVisionRenderer.renderAndSetupFog(profiler);

        profiler.popPush("terrain_setup");
        this.waveVisionSetupRender(camera, frustum, frustumExists, this.minecraft.player.isSpectator());
        profiler.popPush("compilechunks");
        this.compileChunks(camera);

        // Grayscale terrain that's darker further away
        profiler.popPush("terrain");
        ChangedClient.setWaveRenderPhase(ChangedClient.WaveVisionRenderPhase.TERRAIN);
        waveVisionRenderer.renderTerrain(profiler, poseStack, camX, camY, camZ, projectionMatrix);

        if (this.level.effects().constantAmbientLight()) {
            Lighting.setupNetherLevel(poseStack.last().pose());
        } else {
            Lighting.setupLevel(poseStack.last().pose());
        }

        profiler.popPush("entities");
        ChangedClient.setWaveRenderPhase(ChangedClient.WaveVisionRenderPhase.ENTITIES);
        this.renderedEntities = 0;
        this.culledEntities = 0;
        if (this.itemEntityTarget != null) {
            this.itemEntityTarget.clear(Minecraft.ON_OSX);
            this.itemEntityTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }

        if (this.weatherTarget != null) {
            this.weatherTarget.clear(Minecraft.ON_OSX);
        }

        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();

        // Force rendering entities to use the WaveVision shader
        WaveVisionRenderer.WaveVisionBufferSource overwrittenSource = new WaveVisionRenderer.WaveVisionBufferSource(bufferSource);

        this.renderedEntities += waveVisionRenderer.renderEntities(this.level, frustum, camera, poseStack, camX, camY, camZ, partialTicks, overwrittenSource, bufferSource::endLastBatch);

        this.checkPoseStack(poseStack);

        bufferSource.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
        bufferSource.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        bufferSource.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        bufferSource.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
        profiler.popPush("blockentities");
        ChangedClient.setWaveRenderPhase(ChangedClient.WaveVisionRenderPhase.BLOCK_ENTITIES);

        for(LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
            List<BlockEntity> list = levelrenderer$renderchunkinfo.chunk.getCompiledChunk().getRenderableBlockEntities();
            if (!list.isEmpty()) {
                for(BlockEntity blockentity1 : list) {
                    if(!frustum.isVisible(blockentity1.getRenderBoundingBox())) continue;
                    BlockPos blockpos4 = blockentity1.getBlockPos();
                    MultiBufferSource multibuffersource1 = overwrittenSource;
                    poseStack.pushPose();
                    poseStack.translate((double)blockpos4.getX() - camX, (double)blockpos4.getY() - camY, (double)blockpos4.getZ() - camZ);
                    SortedSet<BlockDestructionProgress> sortedset = this.destructionProgress.get(blockpos4.asLong());
                    if (sortedset != null && !sortedset.isEmpty()) {
                        int j1 = sortedset.last().getProgress();
                        if (j1 >= 0) {
                            PoseStack.Pose posestack$pose1 = poseStack.last();
                            VertexConsumer vertexconsumer = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(j1)), posestack$pose1.pose(), posestack$pose1.normal());
                            multibuffersource1 = (p_194349_) -> {
                                VertexConsumer vertexconsumer3 = bufferSource.getBuffer(p_194349_);
                                return p_194349_.affectsCrumbling() ? VertexMultiConsumer.create(vertexconsumer, vertexconsumer3) : vertexconsumer3;
                            };
                        }
                    }

                    this.blockEntityRenderDispatcher.render(blockentity1, partialTicks, poseStack, multibuffersource1);
                    poseStack.popPose();
                }
            }
        }

        synchronized(this.globalBlockEntities) {
            for(BlockEntity blockentity : this.globalBlockEntities) {
                if(!frustum.isVisible(blockentity.getRenderBoundingBox())) continue;
                BlockPos blockpos3 = blockentity.getBlockPos();
                poseStack.pushPose();
                poseStack.translate((double)blockpos3.getX() - camX, (double)blockpos3.getY() - camY, (double)blockpos3.getZ() - camZ);
                this.blockEntityRenderDispatcher.render(blockentity, partialTicks, poseStack, bufferSource);
                poseStack.popPose();
            }
        }

        this.checkPoseStack(poseStack);
        bufferSource.endBatch(RenderType.solid());
        bufferSource.endBatch(RenderType.endPortal());
        bufferSource.endBatch(RenderType.endGateway());
        bufferSource.endBatch(Sheets.solidBlockSheet());
        bufferSource.endBatch(Sheets.cutoutBlockSheet());
        bufferSource.endBatch(Sheets.bedSheet());
        bufferSource.endBatch(Sheets.shulkerBoxSheet());
        bufferSource.endBatch(Sheets.signSheet());
        bufferSource.endBatch(Sheets.chestSheet());
        this.renderBuffers.outlineBufferSource().endOutlineBatch();

        profiler.popPush("destroyProgress");

        for(Long2ObjectMap.Entry<SortedSet<BlockDestructionProgress>> entry : this.destructionProgress.long2ObjectEntrySet()) {
            BlockPos blockpos2 = BlockPos.of(entry.getLongKey());
            double d3 = (double)blockpos2.getX() - camX;
            double d4 = (double)blockpos2.getY() - camY;
            double d5 = (double)blockpos2.getZ() - camZ;
            if (!(d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D)) {
                SortedSet<BlockDestructionProgress> sortedset1 = entry.getValue();
                if (sortedset1 != null && !sortedset1.isEmpty()) {
                    int k1 = sortedset1.last().getProgress();
                    poseStack.pushPose();
                    poseStack.translate((double)blockpos2.getX() - camX, (double)blockpos2.getY() - camY, (double)blockpos2.getZ() - camZ);
                    PoseStack.Pose posestack$pose = poseStack.last();
                    VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(k1)), posestack$pose.pose(), posestack$pose.normal());
                    this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(blockpos2), blockpos2, this.level, poseStack, vertexconsumer1);
                    poseStack.popPose();
                }
            }
        }

        this.checkPoseStack(poseStack);
        HitResult hitresult = this.minecraft.hitResult;
        if (renderBlockOutline && hitresult != null && hitresult.getType() == HitResult.Type.BLOCK) {
            profiler.popPush("outline");
            BlockPos blockpos1 = ((BlockHitResult)hitresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos1);
            if (!net.minecraftforge.client.ForgeHooksClient.onDrawHighlight((LevelRenderer)(Object)this, camera, hitresult, partialTicks, poseStack, bufferSource))
                if (!blockstate.isAir() && this.level.getWorldBorder().isWithinBounds(blockpos1)) {
                    VertexConsumer vertexconsumer2 = bufferSource.getBuffer(RenderType.lines());
                    this.renderHitOutline(poseStack, vertexconsumer2, camera.getEntity(), camX, camY, camZ, blockpos1, blockstate);
                }
        } else if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
            net.minecraftforge.client.ForgeHooksClient.onDrawHighlight((LevelRenderer)(Object)this, camera, hitresult, partialTicks, poseStack, bufferSource);
        }

        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        this.minecraft.debugRenderer.render(poseStack, bufferSource, camX, camY, camZ);
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        bufferSource.endBatch(Sheets.translucentCullBlockSheet());
        bufferSource.endBatch(Sheets.bannerSheet());
        bufferSource.endBatch(Sheets.shieldSheet());
        bufferSource.endBatch(RenderType.armorGlint());
        bufferSource.endBatch(RenderType.armorEntityGlint());
        bufferSource.endBatch(RenderType.glint());
        bufferSource.endBatch(RenderType.glintDirect());
        bufferSource.endBatch(RenderType.glintTranslucent());
        bufferSource.endBatch(RenderType.entityGlint());
        bufferSource.endBatch(RenderType.entityGlintDirect());
        bufferSource.endBatch(RenderType.waterMask());
        this.renderBuffers.crumblingBufferSource().endBatch();
        bufferSource.endBatch(RenderType.lines());
        bufferSource.endBatch();

        posestack.pushPose();
        posestack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        this.renderDebug(camera);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        FogRenderer.setupNoFog();

        ChangedClient.setRenderingWaveVision(false);
    }
}
