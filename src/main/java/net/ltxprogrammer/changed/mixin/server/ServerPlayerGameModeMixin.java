package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AutotoolAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow @Final protected ServerPlayer player;
    @Shadow protected ServerLevel level;
    @Unique protected boolean changed$isDestroyingAlt = false;
    @Unique protected boolean changed$lastBlockHarvested = false;
    @Unique protected Direction changed$lastDirection = Direction.DOWN;
    @Unique protected BlockState changed$lastRemovedBlockState = Blocks.AIR.defaultBlockState();
    @Unique protected ItemStack changed$lastUsedItemToBreak = ItemStack.EMPTY;
    @Unique protected ItemStack changed$preferredDestroyWith = null;

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
        if (!changed$isDestroyingAlt) {
            var autotool = AbstractAbility.getAbilityInstance(instance, ChangedAbilities.AUTOTOOL.get());
            if (autotool == null || !autotool.isActive()) {
                changed$lastUsedItemToBreak = original.call(instance);
                return changed$lastUsedItemToBreak;
            }

            changed$lastUsedItemToBreak = AutotoolAbility.getItemToUse(IAbstractChangedEntity.forPlayer(instance), blockState);
            return changed$lastUsedItemToBreak;
        } else {
            return changed$preferredDestroyWith;
        }
    }

    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    public boolean changed$cacheHarvested(ServerPlayerGameMode instance, BlockPos blockPos, boolean canHarvest, Operation<Boolean> original, @Local BlockState blockState) {
        changed$lastRemovedBlockState = blockState;
        boolean removed = original.call(instance, blockPos, canHarvest);
        changed$lastBlockHarvested = removed && canHarvest;
        return removed;
    }

    @WrapMethod(method = "handleBlockBreakAction")
    public void changed$cacheDirection(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int maxBuildHeight, int sequence, Operation<Void> original) {
        changed$lastDirection = direction;
        original.call(blockPos, action, direction, maxBuildHeight, sequence);
    }

    @WrapMethod(method = "destroyBlock")
    public boolean changed$andDestroyBlockBelow(BlockPos blockPos, Operation<Boolean> original) {
        boolean broke = original.call(blockPos);
        // Only excavate when the block was harvested and was not instabreak
        if (broke && changed$lastBlockHarvested && changed$lastRemovedBlockState.getDestroySpeed(level, blockPos) > 0.0f) {
            var excavate = AbstractAbility.getAbilityInstance(this.player, ChangedAbilities.EXCAVATE.get());
            if (excavate == null || !excavate.isActive())
                return true;

            if (!changed$lastDirection.getAxis().isHorizontal())
                return true;

            var autotool = AbstractAbility.getAbilityInstance(this.player, ChangedAbilities.AUTOTOOL.get());

            BlockPos below = blockPos.below();
            BlockPos occlusionPos = below.relative(changed$lastDirection);
            BlockState occlusionState = level.getBlockState(occlusionPos);
            if (occlusionState.isFaceSturdy(level, occlusionPos, changed$lastDirection.getOpposite(), SupportType.FULL))
                return true; // Face of the bottom block is occluded, don't break

            BlockState belowState = level.getBlockState(below);

            var correctItem = autotool == null || !autotool.isActive() ?
                    AutotoolAbility.getFirstMainHandCorrectItem(IAbstractChangedEntity.forPlayer(this.player), belowState, changed$lastUsedItemToBreak) :
                    AutotoolAbility.getFirstCorrectItem(IAbstractChangedEntity.forPlayer(this.player), belowState, changed$lastUsedItemToBreak);
            if (correctItem == null)
                return true;

            changed$isDestroyingAlt = true;
            changed$preferredDestroyWith = correctItem;
            original.call(below);
            changed$preferredDestroyWith = null;
            changed$isDestroyingAlt = false;
        }
        return broke;
    }
}
