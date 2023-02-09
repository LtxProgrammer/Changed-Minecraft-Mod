package net.ltxprogrammer.changed.effect.particle;

import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TscSweepParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public TscSweepParticle(ClientLevel level, double x, double y, double z, double size, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.sprites = sprites;
        this.lifetime = 4;
        this.rCol = ChangedParticles.Color3.TSC_BLUE.red();
        this.gCol = ChangedParticles.Color3.TSC_BLUE.green();
        this.bCol = ChangedParticles.Color3.TSC_BLUE.blue();
        this.quadSize = 1.0F - (float)size * 0.5F;
        this.setSpriteFromAge(sprites);
    }

    public int getLightColor(float p_105562_) {
        return 15728880;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_105566_) {
            this.sprites = p_105566_;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double size, double p_105583_, double p_105584_) {
            return new TscSweepParticle(level, x, y, z, size, this.sprites);
        }
    }
}