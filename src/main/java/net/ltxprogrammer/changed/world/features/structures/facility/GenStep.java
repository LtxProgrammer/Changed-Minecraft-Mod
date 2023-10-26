package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The position of a glu block
 * @param position
 * @param blockState
 */
public record GenStep(BlockPos position, BlockState blockState, WeightedRandomList<WeightedEntry.Wrapper<PieceType>> validTypes) {
}
