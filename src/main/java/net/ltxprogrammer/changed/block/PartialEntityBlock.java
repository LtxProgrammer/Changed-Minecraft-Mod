package net.ltxprogrammer.changed.block;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface PartialEntityBlock extends EntityBlock {
    boolean stateHasBlockEntity(BlockState blockState);
}
