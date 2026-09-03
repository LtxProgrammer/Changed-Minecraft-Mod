package net.ltxprogrammer.changed.entity.ai.favors;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ai.LatexSuitOwnerGoal;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

public class SuitOwnerFavor extends TamedEntityFavor {
    @Override
    public void createFavorGoals(ChangedEntity entity, LivingEntity owner, GoalConsumer goalConsumer) {
        goalConsumer.accept(1, new LatexSuitOwnerGoal(entity, 0.28, true));
    }

    @Override
    public void favorDeselected(ChangedEntity entity, LivingEntity owner) {
        var grabEntityAbilityInstance = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (owner != null && grabEntityAbilityInstance != null && grabEntityAbilityInstance.grabbedEntity == owner) {
            grabEntityAbilityInstance.releaseEntity(false);
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                    new GrabEntityPacket(entity, owner, GrabEntityPacket.GrabType.RELEASE));
            ChangedSounds.broadcastSound(entity, ChangedSounds.LATEX_UNSUIT_ENTITY, 1.0f, 1.0f);
        }

        if (entity.getTarget() == owner)
            entity.setTarget(null);
    }

    @Override
    public void tickSelectedFavor(ChangedEntity entity, LivingEntity owner) {
        super.tickSelectedFavor(entity, owner);

        var grabAbility = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (grabAbility != null) {
            if (grabAbility.grabbedEntity == owner && grabAbility.grabbedEntity != null) {
                grabAbility.grabbedHasControl = true;
                grabAbility.suited = true;
            }
        }
    }
}
