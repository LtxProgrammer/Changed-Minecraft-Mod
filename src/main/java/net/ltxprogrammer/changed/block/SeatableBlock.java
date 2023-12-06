package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface SeatableBlock {
    Vec3 getSitOffset(BlockState state, BlockPos pos);
}
