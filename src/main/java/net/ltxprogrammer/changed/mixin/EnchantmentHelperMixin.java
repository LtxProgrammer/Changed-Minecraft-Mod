package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method = "hasAquaAffinity", at = @At("HEAD"), cancellable = true)
    private static void hasAquaAffinity(LivingEntity le, CallbackInfoReturnable<Boolean> callback) {
        if (le instanceof Player player && ProcessTransfur.isPlayerLatex(player) && ProcessTransfur.getPlayerLatexVariant(player).breatheMode.hasAquaAffinity())
            callback.setReturnValue(true);
    }
}
