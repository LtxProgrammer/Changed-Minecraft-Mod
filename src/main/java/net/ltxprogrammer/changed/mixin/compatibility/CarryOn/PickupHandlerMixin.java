package net.ltxprogrammer.changed.mixin.compatibility.CarryOn;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.config.Configs;
import tschipp.carryon.common.handler.PickupHandler;

@Mixin(value = PickupHandler.class, remap = false)
public class PickupHandlerMixin {
    @Inject(method = "canPlayerPickUpEntity", at = @At("HEAD"), cancellable = true)
    private static void handleChangedEntities(ServerPlayer player, Entity toPickUp, CallbackInfoReturnable<Boolean> cir) {
        if (!(toPickUp instanceof ChangedEntity changedEntity)) return;
        if (Configs.Settings.pickupHostileMobs.get()) return;

        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            if (variant.getLatexType() != changedEntity.getLatexType())
                cir.setReturnValue(false);
        }, () -> cir.setReturnValue(false));
    }
}
