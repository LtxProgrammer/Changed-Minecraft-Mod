package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> {
    private boolean isLatexed() {
        return getLatexed() != LatexType.NEUTRAL;
    }

    private LatexType getLatexed() {
        return this.getProperties().contains(COVERED) ? this.getValue(COVERED) : LatexType.NEUTRAL;
    }

    protected BlockStateBaseMixin(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    @Inject(method = "isSuffocating", at = @At("HEAD"), cancellable = true)
    public void isSuffocating(BlockGetter getter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (getLatexed() == LatexType.WHITE_LATEX) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "isViewBlocking", at = @At("HEAD"), cancellable = true)
    public void isViewBlocking(BlockGetter getter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (getLatexed() == LatexType.WHITE_LATEX) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
