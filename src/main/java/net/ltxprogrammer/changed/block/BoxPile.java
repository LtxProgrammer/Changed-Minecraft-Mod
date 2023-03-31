package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.init.ChangedBlocks;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class BoxPile extends HorizontalDirectionalBlock implements NonLatexCoverableBlock {
    public static final EnumProperty<QuarterSection> SECTION = EnumProperty.create("section", QuarterSection.class);

    public static final VoxelShape BOX1 = Block.box(1.0D, 0.0D, 2.0D, 13.0D, 10.0D, 14.0D);
    public static final VoxelShape BOX2 = Block.box(-13.0D, 0.0D, 2.0D, -1.0D, 10.0D, 14.0D);
    public static final VoxelShape BOX3 = Block.box(-6.0D, 10.0D, 2.0D, 6.0D, 20.0D, 14.0D);
    public static final VoxelShape ALL_BOXES = Shapes.or(BOX1, BOX2, BOX3);

    public BoxPile() {
        super(BlockBehaviour.Properties.of(Material.WOOL).strength(1.0F).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never)
                .sound(SoundType.SCAFFOLDING));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SECTION, QuarterSection.BOTTOM_LEFT));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, SECTION);
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
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        VoxelShape shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), ALL_BOXES);

        double x = 0.0D;
        double z = 0.0D;

        switch (state.getValue(FACING)) {
            case NORTH -> x = 1.0D;
            case EAST -> z = 1.0D;
            case SOUTH -> x = -1.0D;
            case WEST -> z = -1.0D;
        }

        switch (state.getValue(SECTION)) {
            case BOTTOM_LEFT -> { return shape; }
            case TOP_LEFT -> { return shape.move(0, -1.0D, 0); }
            case TOP_RIGHT -> { return shape.move(x, -1.0D, z); }
            case BOTTOM_RIGHT -> { return shape.move(x, 0.0D, z); }
        }

        return shape;
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
            return this.defaultBlockState().setValue(FACING, direction.getOpposite());
        } else {
            return null;
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
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

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, Either<Boolean, Direction.Axis> allCheckOrAxis) {
        if (allCheckOrAxis.left().isPresent() && !allCheckOrAxis.left().get() && state.getValue(SECTION) == QuarterSection.BOTTOM_LEFT)
            return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheckOrAxis.left().isPresent() && allCheckOrAxis.left().get() ? Arrays.stream(QuarterSection.values()).toList() : thisSect.getOtherValues()) {
            if (allCheckOrAxis.right().isPresent()) {
                if (!thisSect.isOnAxis(sect, state.getValue(FACING), allCheckOrAxis.right().get()))
                    continue;
            }

            var other = level.getBlockState(thisSect.getRelative(pos, state.getValue(FACING), sect));
            if (other.is(this) && other.getValue(SECTION) == sect)
                continue;
            return false;
        }

        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canSurvive(state, level, pos, Either.left(false));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherBlockPos) {
        if (!this.canSurvive(state, level, pos, Either.right(direction.getAxis())))
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
