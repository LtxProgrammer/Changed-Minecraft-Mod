package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.animations.AnimationAssociations;
import net.ltxprogrammer.changed.client.animations.AnimationDefinitions;
import net.ltxprogrammer.changed.client.latexparticles.LatexParticleEngine;
import net.ltxprogrammer.changed.client.latexparticles.SetupContext;
import net.ltxprogrammer.changed.client.renderer.blockentity.ChangedBlockEntityWithoutLevelRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.FirstPersonLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.sound.GasSFX;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.VisionType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ChangedClient {
    private static final Minecraft minecraft = Minecraft.getInstance();
    public static long clientTicks = 0;
    public static final LatexParticleEngine particleSystem = new LatexParticleEngine(minecraft);
    public static final ChangedBlockEntityWithoutLevelRenderer itemRenderer =
            new ChangedBlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    public static final AbilityColors abilityColors = AbilityColors.createDefault();
    public static final AbilityRenderer abilityRenderer = new AbilityRenderer(minecraft.textureManager, minecraft.getModelManager(), abilityColors);

    public static void registerEventListeners() {
        Changed.addEventListener(ChangedClient::afterRenderStage);
        Changed.addEventListener(ChangedClient::onClientTick);
    }

    public static void registerReloadListeners(Consumer<PreparableReloadListener> resourceManager) {
        resourceManager.accept(particleSystem);
        resourceManager.accept(abilityRenderer);
        resourceManager.accept(AnimationDefinitions.INSTANCE);
        resourceManager.accept(AnimationAssociations.INSTANCE);
    }

    public static void afterRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            particleSystem.render(event.getPoseStack(), minecraft.gameRenderer.lightTexture(), event.getCamera(), event.getPartialTick(), event.getFrustum(), SetupContext.THIRD_PERSON);
            FirstPersonLayer.renderFirstPersonLayersOnFace(event.getPoseStack(), event.getCamera(), event.getPartialTick());
        }
    }

    public static double getAcceptableParticleDistanceSqr() {
        return switch (minecraft.options.particles) {
            case ALL -> 9999999999999999.0;
            case DECREASED -> 4096.0;
            case MINIMAL -> 256.0;
            default -> 16384.0; // In case of a mixin
        };
    }

    protected static void addLatexParticleToEntity(ChangedEntity entity) {
        if (particleSystem.pauseForReload())
            return;
        if (entity.getRandom().nextFloat() > entity.getDripRate(1.0f - entity.computeHealthRatio()))
            return;
        if (minecraft.cameraEntity != null && entity.distanceToSqr(minecraft.cameraEntity) > getAcceptableParticleDistanceSqr())
            return;
        var renderer = minecraft.getEntityRenderDispatcher().getRenderer(entity);
        if (!(renderer instanceof LivingEntityRenderer<?,?> livingEntityRenderer))
            return;
        for (var layer : livingEntityRenderer.layers) {
            if (layer instanceof LatexParticlesLayer<?,?> latexParticlesLayer) {
                latexParticlesLayer.createNewDripParticle(entity);
                break;
            }
        }
    }

    protected static void addLatexParticleToEntity(Player entity) {
        ProcessTransfur.ifPlayerTransfurred(entity, variant -> {
            addLatexParticleToEntity(variant.getChangedEntity());
        });
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        if (minecraft.level != null && particleSystem.tick()) {
            var cameraPos = minecraft.gameRenderer.getMainCamera().getBlockPosition();
            var aabb = AABB.of(BoundingBox.fromCorners(cameraPos.offset(-64, -64, -64), cameraPos.offset(64, 64, 64)));
            minecraft.level.getEntitiesOfClass(ChangedEntity.class, aabb).forEach(ChangedClient::addLatexParticleToEntity);
            minecraft.level.getEntitiesOfClass(Player.class, aabb).forEach(ChangedClient::addLatexParticleToEntity);
        }

        clientTicks++;

        GasSFX.ensureGasSfx();
    }

    private static List<Consumer<VertexConsumer>> TRANSLUCENT_CONSUMERS = new ArrayList<>();

    public static void runRecordedTranslucentRender(MultiBufferSource buffers, RenderType renderType) {
        final VertexConsumer buffer = buffers.getBuffer(renderType);
        TRANSLUCENT_CONSUMERS.forEach(consumer -> consumer.accept(buffer));
        TRANSLUCENT_CONSUMERS.clear();
    }

    public static void recordTranslucentRender(MultiBufferSource buffers, RenderType renderType, Consumer<VertexConsumer> consumer) {
        if (renderType == RenderType.translucent()) {
            TRANSLUCENT_CONSUMERS.add(consumer);
        } else {
            consumer.accept(buffers.getBuffer(renderType));
        }
    }

    public enum WaveVisionRenderPhase {
        TERRAIN,
        ENTITIES,
        BLOCK_ENTITIES
    }

    private static WaveVisionRenderPhase phase = WaveVisionRenderPhase.TERRAIN;

    public static WaveVisionRenderPhase getWaveRenderPhase() {
        return phase;
    }

    public static void setWaveRenderPhase(WaveVisionRenderPhase phase) {
        ChangedClient.phase = phase;
    }

    public static boolean shouldRenderingWaveVision() {
        return ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(minecraft.cameraEntity))
                .map(variant -> variant.visionType == VisionType.WAVE_VISION)
                .orElse(false);
    }

    private static boolean renderingWaveVision = false;
    private static float waveEffect = 0.0f;
    private static Vector3f waveResonance = Vector3f.ZERO;
    public static float setupWaveVisionEffect(float partialTicks) {
        float effect = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(minecraft.cameraEntity))
                .filter(variant -> variant.visionType == VisionType.WAVE_VISION)
                .map(TransfurVariantInstance::getTicksInWaveVision)
                .map(ticks -> ticks + partialTicks).orElse(0.0f);

        waveEffect = effect * 0.5f;
        return waveEffect;
    }

    public static void setRenderingWaveVision(boolean renderingWaveVision) {
        ChangedClient.renderingWaveVision = renderingWaveVision;
    }

    public static boolean isRenderingWaveVision() {
        return ChangedClient.renderingWaveVision;
    }

    public static float getWaveEffect() {
        return waveEffect;
    }

    public static void setWaveResonance(Vector3f resonance) {
        ChangedClient.waveResonance = resonance;
    }

    public static void resetWaveResonance() {
        ChangedClient.waveResonance = Vector3f.ZERO;
    }

    public static Vector3f getWaveResonance() {
        return waveResonance;
    }
}
