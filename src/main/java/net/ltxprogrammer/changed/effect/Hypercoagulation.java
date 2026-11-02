package net.ltxprogrammer.changed.effect;

import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Hypercoagulation extends MobEffect {
    public Hypercoagulation() {
        super(MobEffectCategory.HARMFUL, 14688288);
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
        if (ProcessTransfur.isGoo(entity))
            return;
        entity.hurt(ChangedDamageSources.BLOODLOSS.source(entity.level().registryAccess()), 1.0f);
    }
}
