package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(SpreadingSnowyDirtBlock.class)
public abstract class SpreadingSnowyDirtBlockMixin extends SnowyDirtBlock {
    public SpreadingSnowyDirtBlockMixin(Properties p_56640_) {
        super(p_56640_);
    }

    @Inject(method = "canBeGrass", at = @At("HEAD"), cancellable = true)
    private static void canBeGrass(BlockState blockState, LevelReader level, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (blockState.getProperties().contains(COVERED) && blockState.getValue(COVERED) != LatexType.NEUTRAL) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "canPropagate", at = @At("HEAD"), cancellable = true)
    private static void canPropagate(BlockState blockState, LevelReader level, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (blockState.getProperties().contains(COVERED) && blockState.getValue(COVERED) != LatexType.NEUTRAL) {
            callbackInfoReturnable.setReturnValue(false);
        }

        else {
            blockState = level.getBlockState(blockPos);
            if (blockState.getProperties().contains(COVERED) && blockState.getValue(COVERED) != LatexType.NEUTRAL) {
                callbackInfoReturnable.setReturnValue(false);
            }
        }
    }
}
