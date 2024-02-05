package net.ltxprogrammer.changed.mixin.compatibility.FirstPerson;

import dev.tr7zw.firstperson.versionless.FirstPersonBase;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FirstPersonBase.class, remap = false)
public abstract class FirstPersonBaseMixin {
    @Inject(method = "isRenderingPlayer", at = @At("HEAD"), cancellable = true)
    public void maybeOverride(CallbackInfoReturnable<Boolean> cir) {
        if (ChangedCompatibility.frozen_isFirstPersonRendering == null)
            return;
        cir.setReturnValue(ChangedCompatibility.frozen_isFirstPersonRendering);
    }
}
