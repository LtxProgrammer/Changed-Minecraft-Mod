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

    @Inject(method = "setGameModeForPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"))
    public void andApplyFlyAbility(GameType newType, GameType oldType, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(this.player, variant -> {
            if (variant.canCreativeFly()) {
                this.player.getAbilities().mayfly = true;
                if (!this.player.onGround() && newType.isSurvival()) {
                    this.player.getAbilities().flying = true;
                }
            }
        });
    }
}
