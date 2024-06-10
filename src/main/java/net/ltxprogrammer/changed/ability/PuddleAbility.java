package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPup;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PuddleAbility extends SimpleAbility {
    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        if (entity.getChangedEntity() instanceof DarkLatexWolfPup pup) {
            entity.getEntity().playSound(ChangedSounds.POISON, 1, 1);
            pup.setPuddle(true);
        }
    }

    @Override
    public void tick(IAbstractChangedEntity entity) {
        entity.getEntity().setDeltaMovement(0, Math.min(entity.getEntity().getDeltaMovement().y, 0), 0);
        entity.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 5, false, false, false));

        entity.getLevel().getEntitiesOfClass(LivingEntity.class, entity.getChangedEntity().getBoundingBox().inflate(0.25, 0, 0.25)).forEach(caught -> {
            if (caught == entity.getEntity())
                return;
            TransfurVariant<?> variant = TransfurVariant.getEntityVariant(caught);
            if (variant != null && variant.getLatexType() == LatexType.DARK_LATEX)
                return;
            caught.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, false, false));
        });
    }

    @Override
    public void stopUsing(IAbstractChangedEntity entity) {
        if (entity.getChangedEntity() instanceof DarkLatexWolfPup pup) {
            pup.setPuddle(false);
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }
}
