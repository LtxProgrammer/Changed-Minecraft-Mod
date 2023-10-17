package net.ltxprogrammer.changed.effect.particle;


import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatexDripParticle extends TextureSheetParticle {
    private boolean lastOnGround = false;

    protected LatexDripParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprite, Color3 color) {
        super(level, x, y, z, vx, vy, vz);
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
            level.playLocalSound(x, y, z, ChangedSounds.LATEX_DRIP, SoundSource.HOSTILE, 0.025f, 1.0f, true);
            lastOnGround = this.onGround;
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<ColoredParticleOption> {
        protected final SpriteSet sprite;

        public Provider(SpriteSet p_106394_) {
            this.sprite = p_106394_;
        }

        @Nullable
        @Override
        public Particle createParticle(ColoredParticleOption type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new LatexDripParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite, type.getColor());
        }
    }
}