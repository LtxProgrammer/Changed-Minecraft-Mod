package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void getNightVisionScale(LivingEntity p_109109_, float p_109110_, CallbackInfoReturnable<Float> ci) {
        if (p_109109_ instanceof Player player) {
            if (ProcessTransfur.isPlayerLatex(player)) {
                if (ProcessTransfur.getPlayerLatexVariant(player).nightVision) {
                    ci.setReturnValue(1.0f);
                    return;
                }

                if (ProcessTransfur.getPlayerLatexVariant(player).getBreatheMode().canBreatheWater()) {
                    ci.setReturnValue(0.85f);
                    return;
                }
            }
        }
    }
}
