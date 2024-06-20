package net.ltxprogrammer.changed.mixin.compatibility.Leashed;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import plutosion.leashed.util.LeadUtil;

@Mixin(value = LeadUtil.class, remap = false)
public abstract class LeadUtilMixin {
    @Inject(method = "canBeCustomleashed", at = @At("HEAD"), cancellable = true)
    protected static void denyTransfurredPlayers(Mob mobEntity, Player player, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (mobEntity instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null)
            cir.setReturnValue(false);
    }
}
