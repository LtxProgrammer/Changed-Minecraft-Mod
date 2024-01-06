package net.ltxprogrammer.changed.mixin.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.ltxprogrammer.changed.block.PartialEntityBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.getLatexed;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> {
    @Shadow protected abstract BlockState asState();

    @Shadow public abstract Block getBlock();

    protected BlockStateBaseMixin(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    @Inject(method = "hasBlockEntity", at = @At("RETURN"), cancellable = true)
    public void hasPartialBlockEntity(CallbackInfoReturnable<Boolean> callback) {
        var base = (BlockBehaviour.BlockStateBase)(Object)this;

        if (callback.getReturnValue() && this.getBlock() instanceof PartialEntityBlock partial && base instanceof BlockState blockState)
            callback.setReturnValue(partial.stateHasBlockEntity(blockState));
    }

    @Inject(method = "isSuffocating", at = @At("HEAD"), cancellable = true)
    public void isSuffocating(BlockGetter getter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (getLatexed(this.asState()) == LatexType.WHITE_LATEX) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "isViewBlocking", at = @At("HEAD"), cancellable = true)
    public void isViewBlocking(BlockGetter getter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (getLatexed(this.asState()) == LatexType.WHITE_LATEX) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
