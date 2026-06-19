package net.ltxprogrammer.changed.ability.active.flying;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.active.SimpleAbility;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.events.NullCriteria;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedAbilityPointEvents;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;

public class SkyDiveAbility extends SimpleAbility {
    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 4;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return entity.getEntity().isFallFlying() ? ChangedAbilities.WING_FLAP.get().getCoolDown(entity) : 20;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        var wingFlapCharges = entity.getAbilityInstanceSafe(ChangedAbilities.WING_FLAP.get()).map(WingFlapAbilityInstance::getChargesRemaining).orElse(0);
        var variant = entity.getTransfurVariantInstance();

        return entity.getEntity() instanceof Player player
                && !player.onGround()
                && !player.hasEffect(MobEffects.LEVITATION)
                && (variant == null || variant.getFlightStamina() > 0.0d)
                && (!player.isFallFlying() || wingFlapCharges > 0);
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.sky_dive.desc"));

    @Override
    public Component getAbilityName(IAbstractChangedEntity entity) {
        return entity.getEntity().isFallFlying() ? Component.translatable("ability.changed.wing_flap") : super.getAbilityName(entity);
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return entity.getEntity().isFallFlying() ? WingFlapAbility.DESCRIPTION : DESCRIPTION;
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
        int level = this.getAbilityLevel(entity);
        var variant = entity.getTransfurVariantInstance();

        if (self instanceof Player player && self.level().isClientSide) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }

        Vec3 lookAngle = self.getLookAngle();
        Vec3 boostAngle;

        if (!self.isFallFlying() && lookAngle.y > 0.0) {
            boostAngle = new Vec3(lookAngle.x, lookAngle.y * 0.25f, lookAngle.z);
        } else {
            boostAngle = lookAngle;
        }

        double boostSpeed;
        if (self.isFallFlying()) {
            var horiz = entity.getFeatureLevel(ChangedVariantFeatures.WING_FLAP_BONUS_HORIZONTAL.get());
            this.playWingFlapSound(entity, horiz >= 0.35d);
            boostSpeed = 0.175d * (1.0 + horiz);
        } else {
            this.playWingFlapSound(entity, level > 0);
            boostSpeed = 0.2d * (level + 1);
        }

        AbilityTreeInstance.offerPointEvent(entity, ChangedAbilityPointEvents.ON_WING_FLAP.get(), NullCriteria.INSTANCE);
        if (self instanceof Player player && !player.isFallFlying()) {
            player.startFallFlying();
        } else {
            if (variant != null) {
                variant.chargeFlightStamina(2.0d);
                entity.getAbilityInstanceSafe(ChangedAbilities.WING_FLAP.get()).ifPresent(WingFlapAbilityInstance::consumeCharge);
            }
        }

        self.setDeltaMovement(self.getDeltaMovement().add(
                boostAngle.x * boostSpeed,
                boostAngle.y * boostSpeed,
                boostAngle.z * boostSpeed
        ));
    }
}
