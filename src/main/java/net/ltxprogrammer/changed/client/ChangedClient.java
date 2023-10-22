package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.client.latexparticles.LatexParticleEngine;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class ChangedClient {
    public static long clientTicks = 0;
    public static final LatexParticleEngine particleSystem = new LatexParticleEngine(Minecraft.getInstance());

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        particleSystem.tick();
        clientTicks++;
    }
}
