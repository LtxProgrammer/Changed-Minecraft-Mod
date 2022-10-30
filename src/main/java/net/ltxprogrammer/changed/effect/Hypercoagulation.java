package net.ltxprogrammer.changed.effect;

import net.ltxprogrammer.changed.item.Syringe;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Hypercoagulation extends MobEffect {
    public Hypercoagulation() {
        super(MobEffectCategory.HARMFUL, 14688288);
        setRegistryName("hypercoagulation");
    }

    @Override
    public String getDescriptionId() {
        return "effect.changed.hypercoagulation";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        entity.hurt(Syringe.BLOODLOSS, 1.0f);
    }
}
