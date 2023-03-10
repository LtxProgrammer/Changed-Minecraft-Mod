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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class BoxPile extends HorizontalDirectionalBlock implements NonLatexCoverableBlock {
    public static final EnumProperty<QuarterSection> SECTION = EnumProperty.create("section", QuarterSection.class);

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public static final VoxelShape SHAPE_FRAME1 = Block.box(14.0D, 0.0D, 2.0D, 16.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME2 = Block.box(-16.0D, 0.0D, 2.0D, -14.0D, 32.0D, 14.0D);
    public static final VoxelShape SHAPE_FRAME3 = Block.box(-16.0D, 30.0D, 2.0D, 16.0D, 32.0D, 14.0D);

    public BoxPile(Properties p_54120_) {
        super(p_54120_);
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            boolean place;
            switch (direction) {
                case NORTH -> place = level.getBlockState(blockpos.east()).canBeReplaced(context) && level.getBlockState(blockpos.east().above()).canBeReplaced(context);
                case EAST -> place = level.getBlockState(blockpos.south()).canBeReplaced(context) && level.getBlockState(blockpos.south().above()).canBeReplaced(context);
                case SOUTH -> place = level.getBlockState(blockpos.west()).canBeReplaced(context) && level.getBlockState(blockpos.west().above()).canBeReplaced(context);
                case WEST -> place = level.getBlockState(blockpos.north()).canBeReplaced(context) && level.getBlockState(blockpos.north().above()).canBeReplaced(context);
                default -> place = false;
            }

            if (!place) return null;
            return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(OPEN, Boolean.FALSE);
        } else {
            return null;
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(SECTION) == QuarterSection.BOTTOM_LEFT ?
                new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance())) :
                List.of();
    }

    @Override
    public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues())
            level.setBlockAndUpdate(thisSect.getRelative(pos, state.getValue(FACING), sect), state.setValue(SECTION, sect));
    }

    protected BlockState getBlockState(BlockState state, LevelReader level, BlockPos pos, QuarterSection otherSect) {
        if (state.getValue(SECTION) == otherSect)
            return state;
        return level.getBlockState(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), otherSect));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, boolean allCheck) {
        if (!allCheck && state.getValue(SECTION) == QuarterSection.BOTTOM_LEFT)
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheck ? Arrays.stream(QuarterSection.values()).toList() : thisSect.getOtherValues()) {
            var other = level.getBlockState(thisSect.getRelative(pos, state.getValue(FACING), sect));
            if (other.is(this) && other.getValue(SECTION) == sect)
                continue;
            return false;
        }

        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canSurvive(state, level, pos, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherBlockPos) {
        if (!this.canSurvive(state, level, pos, true))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, otherState, level, pos, otherBlockPos);
    }

    protected void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        var section = state.getValue(SECTION);
        if (section != QuarterSection.BOTTOM_LEFT) {
            BlockPos blockpos = section.getRelative(pos, state.getValue(FACING), QuarterSection.BOTTOM_LEFT);
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(SECTION) == QuarterSection.BOTTOM_LEFT) {
                BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                preventCreativeDropFromBottomPart(level, pos, state, player);
            } else if (state.getValue(SECTION) != QuarterSection.BOTTOM_LEFT) {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }
}
