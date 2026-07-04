package net.ltxprogrammer.changed.ability.active.mer;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.active.SimpleAbility;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

import java.util.Collection;
import java.util.Collections;

public class TailWhip extends SimpleAbility {
    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_RELEASE_MINIMUM;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 5;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 60;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getEntity().isVisuallySwimming() && entity.getEntity().isEyeInFluidType(ForgeMod.WATER_TYPE.get());
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.mer.tail_whip.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }

    protected void playTailWhipSound(IAbstractChangedEntity entity, boolean strong) {
        var random = entity.getEntity().getRandom();
        entity.getEntity().playSound(
                ChangedSounds.MER_TAIL_WHIP.get(),
                1.0f,
                (1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F) * (strong ? 1.0f : 1.35f));
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        this.playTailWhipSound(entity, false);

        DamageSource source = ChangedDamageSources.MER_TAIL_WHIP.source(entity.getLevel().registryAccess(), entity.getEntity());
        float damage = (float) entity.getEntity().getAttributeBaseValue(Attributes.ATTACK_DAMAGE);

        AABB entityBox = entity.getEntity().getBoundingBox();
        AABB attackBox = entityBox.inflate(entityBox.getXsize(), 0, entityBox.getZsize());
        entity.getLevel().getEntitiesOfClass(Entity.class, attackBox, EntitySelector.NO_CREATIVE_OR_SPECTATOR).forEach(victim -> {
            if (victim == entity.getEntity())
                return;

            if (victim instanceof LivingEntity livingEntity) {
                var decision = entity.makeLatexAssimilationDecision(TransfurCause.GRAB_REPLICATE, livingEntity);
                var behavior = ProcessTransfur.computeAssimilationBehavior(livingEntity, decision);

                if (behavior != null) {
                    behavior.stepAssimilate();
                    return;
                }
            }

            if (victim.hurt(source, damage)) {
                entity.getEntity().setLastHurtMob(victim);
            }
        });
    }
}
