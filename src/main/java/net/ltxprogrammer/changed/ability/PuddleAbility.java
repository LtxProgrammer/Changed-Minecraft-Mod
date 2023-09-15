package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.beast.DarkLatexPup;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PuddleAbility extends SimpleAbility {
    @Override
    public void startUsing(IAbstractLatex entity) {
        if (entity.getLatexEntity() instanceof DarkLatexPup pup) {
            entity.getEntity().playSound(ChangedSounds.POISON, 1, 1);
            pup.setPuddle(true);
        }
    }

    @Override
    public void tick(IAbstractLatex entity) {
        entity.getEntity().setDeltaMovement(0, Math.min(entity.getEntity().getDeltaMovement().y, 0), 0);
        entity.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 5, false, false, false));

        entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getLatexEntity().getBoundingBox().inflate(0.25, 0, 0.25)).forEach(caught -> {
            if (caught == entity.getEntity())
                return;
            caught.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, false, false));
        });
    }

    @Override
    public void stopUsing(IAbstractLatex entity) {
        if (entity.getLatexEntity() instanceof DarkLatexPup pup) {
            pup.setPuddle(false);
        }
    }

    @Override
    public UseType getUseType(IAbstractLatex entity) {
        return UseType.HOLD;
    }
}
