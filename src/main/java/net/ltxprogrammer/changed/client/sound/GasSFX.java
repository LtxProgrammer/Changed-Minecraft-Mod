package net.ltxprogrammer.changed.client.sound;

import net.ltxprogrammer.changed.fluid.TransfurGas;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public abstract class GasSFX {
    public static class GasSoundInstance extends SimpleSoundInstance implements TickableSoundInstance {
        public GasSoundInstance(SoundEvent event, SoundSource source, float volume, float pitch, double x, double y, double z) {
            super(event.getLocation(), source, volume, pitch, true, 0, SoundInstance.Attenuation.LINEAR, x, y, z, false);
        }

        @Override
        public boolean isStopped() {
            return false;
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }

        @Override
        public void tick() {
            if (Minecraft.getInstance().cameraEntity instanceof LivingEntity livingEntity) {
                this.x = livingEntity.getX();
                this.y = livingEntity.getEyeY();
                this.z = livingEntity.getZ();
                if (TransfurGas.validEntityInGas(livingEntity).isPresent()) {
                    this.volume += 0.00333333f;
                    this.volume = Mth.clamp(this.volume, 0.0f, 1.0f);
                    return;
                }
            }

            this.volume -= 0.00666667f;
            this.volume = Mth.clamp(this.volume, 0.0f, 1.0f);
        }
    }

    private static SoundInstance sfx = null;

    public static void ensureGasSfx() {
        final var soundManager = Minecraft.getInstance().getSoundManager();

        if (sfx == null || !soundManager.isActive(sfx)) {
            sfx = new GasSoundInstance(ChangedSounds.FIRE, SoundSource.AMBIENT, 0.0f, 1.0f, 0.0, 0.0, 1.0);
            soundManager.play(sfx);
        }
    }
}
