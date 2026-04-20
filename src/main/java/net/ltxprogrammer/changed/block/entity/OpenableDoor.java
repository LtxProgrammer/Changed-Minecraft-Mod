package net.ltxprogrammer.changed.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public interface OpenableDoor {
    boolean openDoor(BlockState state, Level level, BlockPos pos);
    boolean closeDoor(BlockState state, Level level, BlockPos pos);
    boolean isOpen(BlockState state, Level level, BlockPos pos);
    AABB getDetectionSize(BlockState state, Level level, BlockPos pos);

    @Nullable
    default ResourceLocation getDeviceIcon(BlockState state, Level level, BlockPos pos) {
        return null;
    }
}
