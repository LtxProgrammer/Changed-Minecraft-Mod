package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCustomShapeTallBlock extends AbstractCustomShapeBlock implements DoubleBlockPlace {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public AbstractCustomShapeTallBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        if (p_54559_.getValue(HALF) == DoubleBlockHalf.LOWER)
            return RenderShape.MODEL;
        else
            return RenderShape.INVISIBLE;
    }

    public void setPlacedBy(Level p_52872_, BlockPos p_52873_, BlockState p_52874_, LivingEntity p_52875_, ItemStack p_52876_) {
        BlockPos blockpos = p_52873_.above();
        p_52872_.setBlock(blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, p_52874_.getValue(FACING)), 3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_52863_) {
        BlockPos blockpos = p_52863_.getClickedPos();
        Level level = p_52863_.getLevel();
        return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(p_52863_) ? super.getStateForPlacement(p_52863_) : null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52901_) {
        super.createBlockStateDefinition(p_52901_);
        p_52901_.add(HALF);
    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockStateOther, LevelAccessor level, BlockPos p_52898_, BlockPos p_52899_) {
        DoubleBlockHalf half = blockState.getValue(HALF);
        if (direction.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (direction == Direction.UP) || blockStateOther.is(this) && blockStateOther.getValue(HALF) != half) {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(level, p_52898_) ? Blocks.AIR.defaultBlockState() : super.updateShape(blockState, direction, blockStateOther, level, p_52898_, p_52899_);
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        if (blockState.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(blockState, level, blockPos);
        } else {
            BlockState blockstate = level.getBlockState(blockPos.below());
            if (blockState.getBlock() != this) return super.canSurvive(blockState, level, blockPos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    public void placeAt(LevelAccessor level, BlockState blockState, BlockPos blockPos, int flag) {
        level.setBlock(blockPos, blockState.setValue(HALF, DoubleBlockHalf.LOWER), flag);
        level.setBlock(blockPos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER), flag);
    }

    protected static void preventCreativeDropFromBottomPart(Level p_52904_, BlockPos p_52905_, BlockState p_52906_, Player p_52907_) {
        DoubleBlockHalf doubleblockhalf = p_52906_.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = p_52905_.below();
            BlockState blockstate = p_52904_.getBlockState(blockpos);
            if (blockstate.is(p_52906_.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                p_52904_.setBlock(blockpos, blockstate1, 35);
                p_52904_.levelEvent(p_52907_, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER ?
                super.getDrops(blockState, builder) :
                List.of();
    }

    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                preventCreativeDropFromBottomPart(level, blockPos, blockState, player);
            }
        }

        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        if (p_60547_.getValue(HALF) == DoubleBlockHalf.LOWER)
            return Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);
        else
            return Block.box(0.0, -16.0, 0.0, 16.0, 16.0, 16.0);
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }
}
