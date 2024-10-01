package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface SeatableBlock {
    Vec3 getSitOffset(BlockGetter level, BlockState state, BlockPos pos);

    default void onEnterSeat(BlockGetter level, BlockState state, BlockPos pos, @NotNull Entity entity) {

    }

    default void onExitSeat(BlockGetter level, BlockState state, BlockPos pos, @NotNull Entity entity) {

    }
}
