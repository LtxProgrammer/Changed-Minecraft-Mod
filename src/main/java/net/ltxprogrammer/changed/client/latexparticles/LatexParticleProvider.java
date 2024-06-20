package net.ltxprogrammer.changed.client.latexparticles;

import net.minecraft.client.particle.SpriteSet;

public interface LatexParticleProvider<T extends LatexParticle> {
    LatexParticleType<T> getParticleType();
    T create(SpriteSet spriteSet);
}
