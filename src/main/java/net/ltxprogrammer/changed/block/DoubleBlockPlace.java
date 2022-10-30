package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface DoubleBlockPlace {
    void placeAt(LevelAccessor p_153174_, BlockState p_153175_, BlockPos p_153176_, int p_153177_);
}
