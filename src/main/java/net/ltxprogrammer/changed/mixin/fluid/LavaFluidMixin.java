package net.ltxprogrammer.changed.mixin.fluid;

import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin extends FlowingFluid {
    @Shadow protected abstract void fizz(LevelAccessor level, BlockPos pos);

    @Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState, CallbackInfo callback) {
        if (direction == Direction.DOWN) {
            FluidState fluidstate = level.getFluidState(pos);
            if (this.is(FluidTags.LAVA) && fluidstate.is(ChangedTags.Fluids.LATEX)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(level, pos, pos, Blocks.AIR.defaultBlockState()), 3);
                }

                this.fizz(level, pos);
                callback.cancel();
            }
        }
    }

    @Inject(method = "canBeReplacedWith", at = @At("HEAD"), cancellable = true)
    protected void canBeReplacedWith(FluidState fluidState, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction, CallbackInfoReturnable<Boolean> callback) {
        if (fluidState.getHeight(level, pos) >= 0.44444445F && fluid.is(ChangedTags.Fluids.LATEX))
            callback.setReturnValue(true);
    }
}
