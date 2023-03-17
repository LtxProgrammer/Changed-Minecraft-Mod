package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.TextBlockEntity;
import net.ltxprogrammer.changed.world.inventory.ClipboardMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClipboardBlock extends AbstractCustomShapeEntityBlock implements TextMenuProvider {
    public static final VoxelShape SHAPE_WHOLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D);

    public ClipboardBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.CANDLE).strength(0.2F));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MenuProvider provider) {
                player.openMenu(provider);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.FAIL;
    }


    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, entity, itemStack);
        if (entity instanceof Player player)
            player.openMenu(getMenuProvider(blockState, level, blockPos));
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block source, BlockPos sourcePos, boolean simulate) {
        super.neighborChanged(blockState, level, blockPos, source, sourcePos, simulate);
        if (!blockState.canSurvive(level, blockPos)) {
            BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            dropResources(blockState, level, blockPos, blockentity);
            level.removeBlock(blockPos, false);
        }
    }

    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE_WHOLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TextBlockEntity(pos, state);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(BlockState blockState, BlockGetter level, BlockPos blockPos, int id, Inventory inv, Player player) {
        if (level.getBlockEntity(blockPos) instanceof TextEnterable textEnterable)
            return new ClipboardMenu(id, inv, blockPos, blockState, textEnterable);
        return null;
    }
}
