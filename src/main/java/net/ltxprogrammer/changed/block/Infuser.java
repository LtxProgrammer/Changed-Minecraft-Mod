package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.world.inventory.InfuserMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class Infuser extends LabBlock {
    public Infuser(Properties p_49795_) {
        super(p_49795_);
    }

    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.changed.infuser");

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((p_52229_, inventory, player) -> new InfuserMenu(p_52229_, inventory, null), CONTAINER_TITLE);
    }
}
