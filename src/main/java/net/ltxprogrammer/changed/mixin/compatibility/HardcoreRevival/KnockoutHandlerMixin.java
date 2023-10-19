package net.ltxprogrammer.changed.mixin.compatibility.HardcoreRevival;

import net.blay09.mods.balm.api.event.LivingDamageEvent;
import net.blay09.mods.hardcorerevival.handler.KnockoutHandler;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KnockoutHandler.class, remap = false)
public abstract class KnockoutHandlerMixin {
    @Inject(method = "onPlayerDamage", at = @At("HEAD"), cancellable = true)
    private static void maybeIgnoreEvent(LivingDamageEvent event, CallbackInfo callback) {
        if (event.getDamageSource() instanceof ChangedDamageSources.TransfurDamageSource)
            callback.cancel();
    }
}
