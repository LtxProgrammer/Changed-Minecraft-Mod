package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.fluid.Gas;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class FreshAirBlock extends AirBlock {
    public FreshAirBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return !(fluid instanceof Gas);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true;
    }
}
