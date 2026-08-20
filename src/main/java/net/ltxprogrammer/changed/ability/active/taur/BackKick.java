package net.ltxprogrammer.changed.ability.active.taur;

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

public class BackKick extends SimpleAbility {
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
        return entity.getEntity().onGround();
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.taur.back_kick.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }

    protected void playKickSound(IAbstractChangedEntity entity, boolean strong) {
        var random = entity.getEntity().getRandom();
        entity.getEntity().playSound(
                ChangedSounds.TAUR_BACK_KICK.get(),
                1.0f,
                (1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F) * (strong ? 1.0f : 1.35f));
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        this.playKickSound(entity, false);

        if (entity.getLevel().isClientSide())
            return;

        DamageSource source = ChangedDamageSources.TAUR_BACK_KICK.source(entity.getLevel().registryAccess(), entity.getEntity());
        float damage = (float) entity.getEntity().getAttributeBaseValue(Attributes.ATTACK_DAMAGE) * 1.2f;

        AABB entityBox = entity.getEntity().getBoundingBox();
        double yRot = Math.toRadians(entity.getEntity().yHeadRot);
        AABB attackBox = entityBox.move(Math.sin(yRot) * 1.2, 0.0, -Math.cos(yRot) * 1.2).inflate(0.25);
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