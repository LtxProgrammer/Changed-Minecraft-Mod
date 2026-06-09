package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.events.NullCriteria;
import net.ltxprogrammer.changed.init.ChangedAbilityPointEvents;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
        return 1;
    }

    @Override
    public void tickIdle() {
        var self = entity.getEntity();

        if (self.onGround() || self.onClimbable())
            charges = this.getMaxCharges();
    }

    @Override
    public void stopUsing() {

    }
}
