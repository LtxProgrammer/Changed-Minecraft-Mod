package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.fluid.Gas;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class AirConditionerBlock extends AbstractCustomShapeBlock {
    public AirConditionerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState otherState, boolean simulate) {
        super.onPlace(state, level, pos, otherState, simulate);

        level.scheduleTick(pos, this, 4);
    }

    public boolean maybeReplaceWithFreshAir(Level level, BlockPos pos) {
        var blockState = level.getBlockState(pos);
        var fluidState = blockState.getFluidState();

        if (blockState.isAir() || blockState.is(ChangedTags.Blocks.GAS)) {
            level.setBlockAndUpdate(pos, ChangedBlocks.FRESH_AIR.get().defaultBlockState());
            return true;
        }

        else if (fluidState.getAmount() >= 7)
            return false;

        else if (blockState.isCollisionShapeFullBlock(level, pos))
            return false;

        return true;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        level.scheduleTick(pos, this, 4);

        if (maybeReplaceWithFreshAir(level, pos.relative(state.getValue(FACING))))
            maybeReplaceWithFreshAir(level, pos.relative(state.getValue(FACING), 2));
    }
}
