package net.ltxprogrammer.changed.mixin.compatibility.Pehkui;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;

@Mixin(value = ScaleType.class, priority = 9999, remap = false)
public abstract class ScaleTypeMixin {
    @Shadow public abstract ScaleData getScaleData(Entity entity);

    @Inject(method = "getScaleData", at = @At("HEAD"), cancellable = true)
    public void getHostScaleData(Entity entity, CallbackInfoReturnable<ScaleData> callback) {
        if (entity instanceof LatexEntity latexEntity && latexEntity.getUnderlyingPlayer() != null)
            callback.setReturnValue(this.getScaleData(latexEntity.getUnderlyingPlayer()));
    }
}
