package net.ltxprogrammer.changed.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.Function;

public interface StateHolderHelper<O, S> {
    Function<Block, BlockState> FN_STATE_CREATION_BYPASS = Block::defaultBlockState;

    StateHolderHelper<O, S> setValueTypeless(Property<?> property, Object value);
    StateHolder<O, S> getState();
}
