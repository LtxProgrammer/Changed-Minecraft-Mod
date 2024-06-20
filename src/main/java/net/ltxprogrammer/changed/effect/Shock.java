package net.ltxprogrammer.changed.effect;

import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class Shock extends MobEffect {
    public static void setNoControlTicks(LivingEntity entity, int ticks) {
        if (entity instanceof LivingEntityDataExtension ext)
            ext.setNoControlTicks(ticks);
    }

    public Shock() {
        super(MobEffectCategory.HARMFUL, 14688288);
        setRegistryName("shock");
    }

    @Override
    public String getDescriptionId() {
        return "effect.changed.shock";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        super.applyEffectTick(livingEntity, amplifier);
        livingEntity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0, 1, 0));
    }
}
