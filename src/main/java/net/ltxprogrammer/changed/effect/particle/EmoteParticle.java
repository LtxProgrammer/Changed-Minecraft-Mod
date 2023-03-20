package net.ltxprogrammer.changed.effect.particle;


import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmoteParticle extends TextureSheetParticle {
    private final Entity track;

    protected EmoteParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprite, Emote emote, Entity track) {
        super(level, x, y, z, vx, vy, vz);
        this.setSize(0.3f, 0.3f);
        this.quadSize = 0.3f;

        this.lifetime = 80;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.gravity = 0.0f;
        this.hasPhysics = false;
        this.track = track;

        this.setSprite(sprite.get(emote.ordinal(), Emote.values().length - 1));
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(track.getX(), track.getDimensions(track.getPose()).height + 0.65, track.getZ());
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<EmoteParticleOption> {
        protected final SpriteSet sprite;

        public Provider(SpriteSet p_106394_) {
            this.sprite = p_106394_;
        }

        @Nullable
        @Override
        public Particle createParticle(EmoteParticleOption type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new EmoteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprite, type.getEmote(), type.getEntity());
        }
    }
}