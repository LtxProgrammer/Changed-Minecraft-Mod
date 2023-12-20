package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.client.latexparticles.LatexParticleEngine;
import net.ltxprogrammer.changed.client.renderer.blockentity.ChangedBlockEntityWithoutLevelRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class ChangedClient {
    private static final Minecraft minecraft = Minecraft.getInstance();
    public static long clientTicks = 0;
    public static final LatexParticleEngine particleSystem = new LatexParticleEngine(minecraft);
    public static final ChangedBlockEntityWithoutLevelRenderer itemRenderer =
            new ChangedBlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());

    public static void registerEventListeners() {
        MinecraftForge.EVENT_BUS.addListener(ChangedClient::afterRenderStage);
        MinecraftForge.EVENT_BUS.addListener(ChangedClient::onClientTick);
    }

    public static void afterRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            particleSystem.render(event.getPoseStack(), minecraft.gameRenderer.lightTexture(), event.getCamera(), event.getPartialTick(), event.getFrustum());
    }

    protected static void addLatexParticleToEntity(LatexEntity entity) {
        if (entity.getRandom().nextFloat() > entity.getDripRate(1.0f - entity.computeHealthRatio()))
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
        ProcessTransfur.ifPlayerLatex(entity, variant -> {
            addLatexParticleToEntity(variant.getLatexEntity());
        });
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (minecraft.level != null) {
            var cameraPos = minecraft.gameRenderer.getMainCamera().getBlockPosition();
            var aabb = AABB.of(BoundingBox.fromCorners(cameraPos.offset(-64, -64, -64), cameraPos.offset(64, 64, 64)));
            minecraft.level.getEntitiesOfClass(LatexEntity.class, aabb).forEach(ChangedClient::addLatexParticleToEntity);
            minecraft.level.getEntitiesOfClass(Player.class, aabb).forEach(ChangedClient::addLatexParticleToEntity);
        }

        particleSystem.tick();
        clientTicks++;
    }
}
