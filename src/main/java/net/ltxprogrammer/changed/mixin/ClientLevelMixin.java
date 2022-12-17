package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Inject(method = "getSkyDarken", at = @At("RETURN"), cancellable = true)
    private void getBrightness(float f, CallbackInfoReturnable<Float> callback) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (ProcessTransfur.isPlayerLatex(player) && ProcessTransfur.getPlayerLatexVariant(player).getLatexType() == LatexType.DARK_LATEX) {
            callback.setReturnValue(callback.getReturnValue() * 0.2F);
        }
    }
}
