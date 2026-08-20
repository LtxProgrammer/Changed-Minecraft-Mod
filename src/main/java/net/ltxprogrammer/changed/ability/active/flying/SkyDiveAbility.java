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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class SkyDiveAbility extends SimpleAbility {
    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return entity.getEntity().isFallFlying() ? UseType.INSTANT : UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 4;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return ChangedAbilities.WING_FLAP.get().getCoolDown(entity);
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        var wingFlapCharges = entity.getAbilityInstanceSafe(ChangedAbilities.WING_FLAP.get()).map(WingFlapAbilityInstance::getCharges).orElse(0);
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

        if (!self.isFallFlying()) {
            if (self instanceof Player player && self.level().isClientSide) {
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }

            Vec3 lookAngle = self.getLookAngle();
            Vec3 boostAngle;

            if (lookAngle.y > 0.0) {
                boostAngle = new Vec3(lookAngle.x, lookAngle.y * 0.25f, lookAngle.z);
            } else {
                boostAngle = lookAngle;
            }

            double boostSpeed = 0.2d * (level + 1);
            this.playWingFlapSound(entity, level > 0);

            AbilityTreeInstance.offerPointEvent(entity, ChangedAbilityPointEvents.ON_WING_FLAP.get(), NullCriteria.INSTANCE);
            if (self instanceof Player player && !player.isFallFlying()) {
                player.startFallFlying();
            }

            self.setDeltaMovement(self.getDeltaMovement().add(
                    boostAngle.x * boostSpeed,
                    boostAngle.y * boostSpeed,
                    boostAngle.z * boostSpeed
            ));

            entity.getAbilityInstanceSafe(ChangedAbilities.WING_FLAP.get()).ifPresent(ability -> ability.getController().applyCoolDown());
        } else { // Forward further ability activations to wing flap ability
            var wingFlap = entity.getAbilityInstance(ChangedAbilities.WING_FLAP.get());
            if (wingFlap != null)
                wingFlap.fallFlyWingBoost();
        }
    }

    @Override
    public @Nullable Integer getCharges(IAbstractChangedEntity entity) {
        var wingFlap = entity.getAbilityInstance(ChangedAbilities.WING_FLAP.get());
        if (!entity.getEntity().isFallFlying() || wingFlap == null)
            return null;
        return wingFlap.getCharges();
    }
}
