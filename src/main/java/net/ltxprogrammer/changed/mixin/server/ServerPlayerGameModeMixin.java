package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AutotoolAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
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

    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack changed$getAutotoolItem(ServerPlayer instance, Operation<ItemStack> original, @Local BlockState blockState) {
        var autotool = AbstractAbility.getAbilityInstance(instance, ChangedAbilities.AUTOTOOL.get());
        if (autotool == null || !autotool.isActive())
            return original.call(instance);

        return AutotoolAbility.getItemToUse(IAbstractChangedEntity.forPlayer(instance), blockState);
    }
}
