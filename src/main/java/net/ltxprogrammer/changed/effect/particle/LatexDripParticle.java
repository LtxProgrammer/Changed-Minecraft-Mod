package net.ltxprogrammer.changed.effect.particle;


import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatexDripParticle extends TextureSheetParticle {
    private boolean lastOnGround = false;

    protected LatexDripParticle(ClientLevel p_108328_, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprite, ChangedParticles.Color3 color) {
        super(p_108328_, x, y, z, vx, vy, vz);
        this.setSize(0.07f, 0.07f);
        this.quadSize *= 0.28f;

        this.lifetime = 100;

        this.gravity = 0.65f;
        this.hasPhysics = true;

        this.xd = vx * 1;
        this.yd = vy * 1;
        this.zd = vz * 1;

        this.setColor(color.red(), color.green(), color.blue());

        this.pickSprite(sprite);
    }

    @Override
    public void tick() {
        super.tick();
        if (!lastOnGround && this.onGround) {
            level.playLocalSound(x, y, z, new SoundEvent(new ResourceLocation("minecraft:entity.slime.squish_small")),
                    SoundSource.HOSTILE, 0.05f, 1.0f, true);
            lastOnGround = this.onGround;
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;
        protected static ChangedParticles.Color3 nextColor = null;

        public Provider(SpriteSet p_106394_) {
            this.sprite = p_106394_;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            var ret = new LatexDripParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite, nextColor);
            nextColor = null;
            return ret;
        }

        public static void setNextColor(ChangedParticles.Color3 next) {
            nextColor = next;
        }

        public static void setNextColor(String next) {
            nextColor = ChangedParticles.Color3.getColor(next);
        }
    }

    public static void setNextColor(ChangedParticles.Color3 next) {
        Provider.nextColor = next;
    }

    public static void setNextColor(String next) {
        Provider.nextColor = ChangedParticles.Color3.getColor(next);
    }
}