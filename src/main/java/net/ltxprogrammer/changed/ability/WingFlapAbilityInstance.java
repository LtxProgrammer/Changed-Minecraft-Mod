package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.events.NullCriteria;
import net.ltxprogrammer.changed.init.ChangedAbilityPointEvents;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class WingFlapAbilityInstance extends AbstractAbilityInstance {
    private int charges = 0;

    public WingFlapAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    protected void playWingFlapSound(boolean strong) {
        var random = entity.getEntity().getRandom();
        entity.getEntity().playSound(
                ChangedSounds.WING_FLAP.get(),
                1.0f,
                (1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F) * (strong ? 1.0f : 1.35f));
    }

    @Override
    public boolean canUse() {
        var self = entity.getEntity();

        if (self instanceof Player player && player.getAbilities().flying)
            return false;

        return charges > 0 &&
                !self.onGround() &&
                !self.onClimbable() &&
                !self.isSwimming() &&
                !self.isFallFlying();
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    @Override
    public void startUsing() {
        var self = entity.getEntity();
        var instance = entity.getTransfurVariantInstance();
        var deltaMovement = self.getDeltaMovement();
        double dy = deltaMovement.y;
        var lookAngle = self.getLookAngle().multiply(2.0d, 1.0d, 2.0d).normalize();
        double horizontalBoost = instance == null ? 0.35d : instance.getFeatureLevel(ChangedVariantFeatures.WING_FLAP_BONUS_HORIZONTAL.get());

        dy = Math.min(
                dy + 1.2d,
                Math.max(dy, 0.5d)
        );

        self.setDeltaMovement(new Vec3(
                deltaMovement.x + lookAngle.x * horizontalBoost,
                dy,
                deltaMovement.z + lookAngle.z * horizontalBoost));
        AbilityTreeInstance.offerPointEvent(entity, ChangedAbilityPointEvents.ON_WING_FLAP.get(), NullCriteria.INSTANCE);
        this.playWingFlapSound(false);
        this.charges -= 1;
    }

    @Override
    public void tick() {

    }

    protected int getMaxCharges() {
        var variant = entity.getTransfurVariantInstance();
        if (variant == null)
            return 1;
        return 1 + (int)variant.getFeatureLevel(ChangedVariantFeatures.WING_FLAP_BONUS_CHARGES.get());
    }

    public int getChargesRemaining() {
        return charges;
    }

    public boolean consumeCharge() {
        if (charges > 0) {
            charges--;
            return true;
        }

        return false;
    }

    @Override
    public void tickIdle() {
        var self = entity.getEntity();
        var variant = entity.getTransfurVariantInstance();

        if (self.onGround() || self.onClimbable())
            charges = this.getMaxCharges();

        if (variant != null && self.getDeltaMovement().y < -1.3d && variant.hasFeature(ChangedVariantFeatures.AUTONOMOUS_LANDING.get())) {
            var collisionContext = CollisionContext.of(self);
            var checkBounding = self.getBoundingBox().move(0.0, -2.0, 0.0);
            boolean nearGround = BlockPos.betweenClosedStream(checkBounding).anyMatch(blockPos -> {
                BlockState state = self.level().getBlockState(blockPos);
                return !state.getCollisionShape(self.level(), blockPos, collisionContext).isEmpty();
            });

            if (nearGround && (variant.getFlightStamina() > 4.0d || consumeCharge())) {
                variant.chargeFlightStamina(4.0d);
                getController().applyCoolDown();

                var deltaMovement = self.getDeltaMovement();
                double dy = deltaMovement.y;

                dy = Math.min(
                        dy + 1.8d,
                        Math.max(dy, 0.5d)
                );

                self.setDeltaMovement(new Vec3(deltaMovement.x, dy, deltaMovement.z));
                AbilityTreeInstance.offerPointEvent(entity, ChangedAbilityPointEvents.ON_WING_FLAP.get(), NullCriteria.INSTANCE);
                self.fallDistance *= 0.1f;

                this.playWingFlapSound(true);
            }
        }
    }

    @Override
    public void stopUsing() {

    }
}
