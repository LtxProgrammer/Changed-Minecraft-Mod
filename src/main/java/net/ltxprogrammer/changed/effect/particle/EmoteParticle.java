package net.ltxprogrammer.changed.effect.particle;


import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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
        this.setPos(track.getX(), track.getY() + track.getDimensions(track.getPose()).height + 0.65, track.getZ());
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTicks) { // The same as SingleQuadParticle, but forces FULL_BRIGHT
        Vec3 camPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());
        Quaternion quat;
        if (this.roll == 0.0F) {
            quat = camera.rotation();
        } else {
            quat = new Quaternion(camera.rotation());
            float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);
            quat.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quat);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quat);
            vector3f.mul(f4);
            vector3f.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        buffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(LightTexture.FULL_BRIGHT).endVertex();
        buffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(LightTexture.FULL_BRIGHT).endVertex();
        buffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(LightTexture.FULL_BRIGHT).endVertex();
        buffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(LightTexture.FULL_BRIGHT).endVertex();
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