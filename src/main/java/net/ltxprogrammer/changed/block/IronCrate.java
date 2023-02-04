package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronCrate extends AbstractCustomShapeTallBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 24.0D, 14.0D);
    public static final VoxelShape SHAPE_OCC = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);

    public IronCrate(Properties properties) {
        super(properties);
    }


    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        if (p_52783_.getValue(HALF) == DoubleBlockHalf.LOWER)
            return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
        else
            return super.canSurvive(p_52783_, p_52784_, p_52785_);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return switch (p_60547_.getValue(HALF)) {
            case UPPER -> SHAPE_WHOLE.move(0.0, -1.0, 0.0);
            case LOWER -> SHAPE_WHOLE;
        };
    }


}
