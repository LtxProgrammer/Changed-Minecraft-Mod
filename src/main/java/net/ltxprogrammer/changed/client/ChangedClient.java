package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.client.latexparticles.LatexParticleEngine;
import net.minecraft.client.Minecraft;
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

    public static void registerEventListeners() {
        MinecraftForge.EVENT_BUS.addListener(ChangedClient::afterRenderStage);
        MinecraftForge.EVENT_BUS.addListener(ChangedClient::onClientTick);
    }

    public static void afterRenderStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            particleSystem.render(event.getPoseStack(), minecraft.gameRenderer.lightTexture(), event.getCamera(), event.getPartialTick(), event.getFrustum());
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        particleSystem.tick();
        clientTicks++;
    }
}
