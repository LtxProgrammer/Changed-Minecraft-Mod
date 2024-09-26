package net.ltxprogrammer.changed.mixin.client;

import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"), cancellable = true)
    public void orDontTurnPlayer(CallbackInfo ci) {
        if (this.minecraft.player instanceof LivingEntityDataExtension ext && ext.getNoControlTicks() > 0)
            ci.cancel();
    }
}
