package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class Gas extends ForgeFlowingFluid {
    protected Gas(Properties properties) {
        super(properties);
    }

    public abstract Color3 getColor();

    @Override
    protected FluidState getNewLiquid(LevelReader level, BlockPos pos, BlockState state) {
        // Overwritten from FlowingFluid.getNewLiquid()

        int i = 0;
        int j = 0;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            FluidState fluidstate = blockstate.getFluidState();
            if (fluidstate.getType().isSame(this) && this.canPassThroughWall(direction, level, pos, state, blockpos, blockstate)) {
                if (fluidstate.isSource() && net.minecraftforge.event.ForgeEventFactory.canCreateFluidSource(level, blockpos, blockstate, this.canConvertToSource())) {
                    ++j;
                }

                i = Math.max(i, fluidstate.getAmount());
            }
        }

        if (j >= 2) {
            BlockState blockstate1 = level.getBlockState(pos.below());
            FluidState fluidstate1 = blockstate1.getFluidState();
            if (blockstate1.getMaterial().isSolid() || this.isSourceBlockOfThisType(fluidstate1)) {
                return this.getSource(false);
            }
        }

        BlockPos blockpos1 = pos.above();
        BlockState blockstate2 = level.getBlockState(blockpos1);
        FluidState fluidstate2 = blockstate2.getFluidState();
        if (!fluidstate2.isEmpty() && fluidstate2.getType().isSame(this) && this.canPassThroughWall(Direction.UP, level, pos, state, blockpos1, blockstate2)) {
            return this.getFlowing(fluidstate2.getAmount(), false); // Prevents amount from resetting to 8 when going down
        } else {
            int k = i - this.getDropOff(level);
            return k <= 0 ? Fluids.EMPTY.defaultFluidState() : this.getFlowing(k, false);
        }
    }

    @Override
    protected boolean isWaterHole(BlockGetter level, Fluid fluid, BlockPos pos, BlockState state, BlockPos otherPos, BlockState otherState) {
        return false; // Allows gas to spread on top of itself
    }

    @Override
    protected boolean canSpreadTo(BlockGetter level, BlockPos pos, BlockState state, Direction direction, BlockPos otherPos, BlockState otherState, FluidState otherFluidState, Fluid fluid) {
        return super.canSpreadTo(level, pos, state, direction, otherPos, otherState, otherFluidState, fluid)
                && !otherState.is(ChangedBlocks.FRESH_AIR.get())
                && (otherState.is(Blocks.AIR) || otherState.is(Blocks.CAVE_AIR))
                && (otherFluidState.isEmpty() || otherFluidState.is(this));
    }
}
