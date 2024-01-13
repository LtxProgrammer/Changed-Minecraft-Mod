package net.ltxprogrammer.changed.mixin.server;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow @Final protected ServerPlayer player;

    @Inject(method = "setGameModeForPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;onUpdateAbilities()V"))
    public void andApplyFlyAbility(GameType newType, GameType oldType, CallbackInfo callback) {
        ProcessTransfur.ifPlayerLatex(this.player, variant -> {
            if (variant.getParent().canGlide) {
                this.player.getAbilities().mayfly = true;
                if (!this.player.isOnGround() && newType.isSurvival()) {
                    this.player.getAbilities().flying = true;
                }
            }
        });
    }
}
