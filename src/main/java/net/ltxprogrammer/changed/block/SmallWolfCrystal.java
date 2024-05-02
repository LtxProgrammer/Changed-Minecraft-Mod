package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.getLatexed;


public class SmallWolfCrystal extends AbstractLatexCrystal {
    public SmallWolfCrystal(Properties p_53514_) {
        super(TransfurVariant.LATEX_CRYSTAL_WOLF, ChangedItems.LATEX_WOLF_CRYSTAL_FRAGMENT, p_53514_);
    }
    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter p_51043_, BlockPos p_51044_) {
        return blockState.getBlock() instanceof LatexWolfCrystalBlock || blockState.getBlock() instanceof DarkLatexBlock || getLatexed(blockState) == LatexType.DARK_LATEX;
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        BlockState blockStateOn = level.getBlockState(blockPos.below());
        if (!canSupportRigidBlock(level, blockPos.below()))
            return false;
        return blockStateOn.getBlock() instanceof LatexWolfCrystalBlock || blockStateOn.getBlock() instanceof DarkLatexBlock || getLatexed(blockStateOn) == LatexType.DARK_LATEX;
    }
}