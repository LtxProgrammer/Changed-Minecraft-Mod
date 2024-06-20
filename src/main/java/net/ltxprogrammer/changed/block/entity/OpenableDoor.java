package net.ltxprogrammer.changed.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public interface OpenableDoor {
    boolean openDoor(BlockState state, Level level, BlockPos pos);
    boolean closeDoor(BlockState state, Level level, BlockPos pos);
    boolean isOpen(BlockState state, Level level, BlockPos pos);
    AABB getDetectionSize(BlockState state, Level level, BlockPos pos);
}
