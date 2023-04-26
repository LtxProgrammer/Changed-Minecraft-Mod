package net.ltxprogrammer.changed.mixin.block;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.isLatexed;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {
    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I", at = @At("HEAD"), cancellable = true)
    public void getColor(BlockState p_92583_, Level p_92584_, BlockPos p_92585_, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (isLatexed(p_92583_)) {
            callbackInfoReturnable.setReturnValue(0xFFFFFFFF);
        }
    }

    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At("HEAD"), cancellable = true)
    public void getColor(BlockState p_92578_, @Nullable BlockAndTintGetter p_92579_, @Nullable BlockPos p_92580_, int p_92581_, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (isLatexed(p_92578_)) {
            callbackInfoReturnable.setReturnValue(0xFFFFFFFF);
        }
    }
}
