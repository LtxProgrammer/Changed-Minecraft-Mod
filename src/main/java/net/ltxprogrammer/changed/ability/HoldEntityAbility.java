package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public class HoldEntityAbility extends AbstractAbility<SimpleAbilityInstance> {
    public HoldEntityAbility() {
        super(SimpleAbilityInstance::new);
    }

    public LivingEntity getHoveredEntity(IAbstractChangedEntity entity) {
        if (!(entity.getEntity() instanceof Player player))
            return null;

        if (!UniversalDist.isLocalPlayer(player))
            return null;

        var hitResult = UniversalDist.getLocalHitResult();
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity)
            return livingEntity;
        return null;
    }

    @Override
    public void tickCharge(IAbstractChangedEntity entity, float ticks) {
        var enemy = this.getHoveredEntity(entity);
        if (enemy != null) {
            entity.getEntity().xxa = (float)entity.getEntity().getLookAngle().x;
            entity.getEntity().zza = (float)entity.getEntity().getLookAngle().z;
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        if (entity.getEntity() instanceof PlayerDataExtension ext) {
            var enemy = this.getHoveredEntity(entity);
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab((Player)entity.getEntity(), enemy));
        }
    }

    @Override
    public void stopUsing(IAbstractChangedEntity entity) {
        super.stopUsing(entity);
    }
}
