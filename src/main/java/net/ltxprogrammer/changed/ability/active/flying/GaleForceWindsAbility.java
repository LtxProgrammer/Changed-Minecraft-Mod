package net.ltxprogrammer.changed.ability.active.flying;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.active.SimpleAbility;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.events.NullCriteria;
import net.ltxprogrammer.changed.entity.projectile.WindGust;
import net.ltxprogrammer.changed.init.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;

public class GaleForceWindsAbility extends SimpleAbility {
    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_RELEASE_MINIMUM;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 10;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 120;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.gale_force_winds.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }

    protected void playWingFlapSound(IAbstractChangedEntity entity, boolean strong) {
        var random = entity.getEntity().getRandom();
        entity.getEntity().playSound(
                ChangedSounds.WING_FLAP.get(),
                1.0f,
                (1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F) * (strong ? 1.0f : 1.35f));
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        var self = entity.getEntity();

        double pushBackSpeed = self.onGround() || self.onClimbable() ? 0.0d : -0.5d;

        Vec3 lookAngle = self.getLookAngle();

        if (!self.level().isClientSide) {
            WindGust nParticle = new WindGust(ChangedEntities.WIND_GUST.get(), self.level());

            nParticle.setOwner(self);
            nParticle.setStrength((int) entity.getFeatureLevel(ChangedVariantFeatures.WINDS_PUSH_STRENGTH.get()));
            nParticle.setPos(self.getEyePosition()
                    .add(lookAngle.multiply(0.75, 0.75, 0.75))
                    .add(0, -0.5, 0)
            );

            nParticle.shootFromRotation(self, self.getXRot(), self.getYRot(), 0.0F, 1.5F, 1.0F);
            self.level().addFreshEntity(nParticle);
        }

        self.setDeltaMovement(self.getDeltaMovement().add(
                lookAngle.x * pushBackSpeed,
                lookAngle.y * pushBackSpeed,
                lookAngle.z * pushBackSpeed
        ));

        AbilityTreeInstance.offerPointEvent(entity, ChangedAbilityPointEvents.ON_WING_FLAP.get(), NullCriteria.INSTANCE);
        this.playWingFlapSound(entity, true);
    }
}
