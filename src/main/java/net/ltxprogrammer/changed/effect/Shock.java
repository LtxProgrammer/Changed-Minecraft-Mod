package net.ltxprogrammer.changed.effect;

import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class Shock extends MobEffect {
    private static Field noControlField;

    static {
        try {
            noControlField = LivingEntity.class.getField("controlDisabledFor");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void setNoControlTicks(LivingEntity entity, int ticks) {
        try {
            noControlField.set(entity, ticks);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
