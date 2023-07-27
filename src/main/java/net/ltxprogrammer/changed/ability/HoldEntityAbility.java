package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public class HoldEntityAbility extends AbstractAbility<SimpleAbilityInstance> {
    public HoldEntityAbility() {
        super(SimpleAbilityInstance::new);
    }

    public LivingEntity getHoveredEntity(Player player) {
        if (!UniversalDist.isLocalPlayer(player))
            return null;

        var hitResult = UniversalDist.getLocalHitResult();
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity)
            return livingEntity;
        return null;
    }

    @Override
    public void tickCharge(Player player, LatexVariantInstance<?> variant, float ticks) {
        var entity = this.getHoveredEntity(player);
        if (entity != null) {
            player.xxa = (float)player.getLookAngle().x;
            player.zza = (float)player.getLookAngle().z;
        }
    }

    @Override
    public UseType getUseType(Player player, LatexVariantInstance<?> variant) {
        return UseType.INSTANT;
    }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        if (player instanceof PlayerDataExtension ext) {
            var entity = this.getHoveredEntity(player);
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab(player, entity));
        }
    }

    @Override
    public void stopUsing(Player player, LatexVariantInstance<?> variant) {
        super.stopUsing(player, variant);
    }
}
