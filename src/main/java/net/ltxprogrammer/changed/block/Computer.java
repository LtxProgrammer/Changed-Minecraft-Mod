package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class Computer extends AbstractCustomShapeBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE_WHOLE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 12.0D, 13.0D);

    public Computer(Properties properties) {
        super(properties);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.DESTROY;
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        super.setPlacedBy(p_52749_, p_52750_, p_52751_, p_52752_, p_52753_);
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        return super.getRenderShape(p_54559_);
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }

    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
    }

    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.changed.computer");

    public static boolean tryUseDisk(@Nullable Player p_153567_, Level p_153568_, BlockPos p_153569_, BlockState p_153570_, ItemStack p_153571_) {
        if (p_153567_ != null) {
            p_153567_.openMenu(p_153570_.getMenuProvider(p_153568_, p_153569_));
            return true;
        }

        return false;
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((p_52229_, inventory, player) -> {
            return new ComputerMenu(p_52229_, inventory, null).setDisk(player.getItemInHand(player.getUsedItemHand()));
        }, CONTAINER_TITLE);
    }
}
