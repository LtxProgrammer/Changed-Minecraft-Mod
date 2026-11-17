package net.ltxprogrammer.changed.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemotePlayer.class)
public abstract class RemotePlayerMixin extends AbstractClientPlayer implements PlayerDataExtension, LivingEntityDataExtension {
    public RemotePlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    public void aiStep(CallbackInfo ci) {
        var playerMover = getPlayerMover();
        if (playerMover != null) {
            playerMover.remoteAiStep((RemotePlayer)(Object)this, InputWrapper.empty(), LogicalSide.CLIENT);
            super.aiStep();
            ci.cancel();
        }
    }
}
