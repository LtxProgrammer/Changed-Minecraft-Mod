package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowBlockMixin {
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void canEntityWalkOnPowderSnow(Entity p_154256_, CallbackInfoReturnable<Boolean> callback) {
        if (p_154256_ instanceof PowderSnowWalkable) {
            callback.setReturnValue(true);
        } else ProcessTransfur.ifPlayerLatex(Util.playerOrNull(p_154256_), variant -> {
            if (variant.getLatexEntity() instanceof PowderSnowWalkable)
                callback.setReturnValue(true);
        });
    }
}
