package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTamedEntityFavors;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraftforge.network.PacketDistributor;

public class LatexSuitOwnerGoal extends MeleeAttackGoal {
    protected final ChangedEntity entity;

    public LatexSuitOwnerGoal(ChangedEntity entity, double speedModifier, boolean visualPersistence) {
        super(entity, speedModifier, visualPersistence);

        this.entity = entity;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double distanceSquared) {
        var ability = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability == null) {
            entity.setTarget(null);
            entity.setFavor(ChangedTamedEntityFavors.NONE.get());
            return;
        }

        if (target == entity.getOwner()) {
            double reachSqr = this.getAttackReachSqr(target) * 0.9;

            if (distanceSquared <= reachSqr && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();

                if (ability.suitEntity(target)) {
                    ability.grabbedHasControl = true;
                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                            new GrabEntityPacket(entity, target, GrabEntityPacket.GrabType.SUIT));
                    ChangedSounds.broadcastSound(entity, ChangedSounds.LATEX_SUIT_ENTITY, 1.0f, 1.0f);
                }
            }
        } else {
            // Re-evaluate nearby entities
            entity.setTarget(null);
        }
    }

    @Override
    public boolean canUse() {
        if (this.entity.getCurrentFavor() != ChangedTamedEntityFavors.SUIT_OWNER.get())
            return false;
        var owner = this.entity.getOwner();
        if (owner == null)
            return false;

        var ability = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability == null || ability.grabbedEntity == owner)
            return false;

        if (ProcessTransfur.isPlayerTransfurred(EntityUtil.playerOrNull(owner)))
            return false;

        this.entity.setTarget(owner);
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getCurrentFavor() != ChangedTamedEntityFavors.SUIT_OWNER.get())
            return false;
        var owner = this.entity.getOwner();
        if (owner == null)
            return false;

        var ability = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability == null || ability.grabbedEntity != owner)
            return false;

        if (ProcessTransfur.isPlayerTransfurred(EntityUtil.playerOrNull(owner)))
            return false;

        this.entity.setTarget(owner);
        return super.canContinueToUse();
    }
}