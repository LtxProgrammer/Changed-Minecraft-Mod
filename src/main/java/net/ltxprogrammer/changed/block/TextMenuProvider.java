package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface TextMenuProvider {
    @Nullable AbstractContainerMenu createMenu(BlockState blockState, BlockGetter level, BlockPos blockPos, int id, Inventory inv, Player player);
}
