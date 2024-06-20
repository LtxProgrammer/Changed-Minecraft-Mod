package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public interface TextMenuProvider {
    @Nullable AbstractContainerMenu createMenu(BlockState blockState, BlockGetter level, BlockPos blockPos, int id, Inventory inv, Player player);

    default void openMenu(Player player, MenuProvider provider, BlockPos blockPos) {
        if (player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, provider, blockPos);
        }
    }

    default void openMenu(ServerPlayer player, MenuProvider provider, BlockPos blockPos) {
        NetworkHooks.openGui(player, provider, extra -> {
            player.level.getBlockEntity(blockPos, ChangedBlockEntities.TEXT_BLOCK_ENTITY.get()).ifPresent(blockEntity -> {
                extra.writeUtf(blockEntity.text);
            });
        });
    }
}
