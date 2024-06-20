package net.ltxprogrammer.changed.effect.particle;


import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GasParticle extends TextureSheetParticle {
    protected GasParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprite, Color3 color) {
        super(level, x, y, z, vx, vy, vz);
        this.setSize(0.07f, 0.07f);
        this.quadSize *= 0.7f;

        this.lifetime = 20;

        this.gravity = 0.015f;
        this.hasPhysics = true;

        this.xd = vx * 1;
        this.yd = vy * 1;
        this.zd = vz * 1;

        this.setColor(color.red(), color.green(), color.blue());

        this.pickSprite(sprite);
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
            return new GasParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite, type.getColor());
        }
    }
}