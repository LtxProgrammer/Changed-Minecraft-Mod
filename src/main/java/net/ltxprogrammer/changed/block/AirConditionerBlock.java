package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.fluid.Gas;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AirConditionerBlock extends AbstractCustomShapeBlock {
    public static final EnumProperty<SixSection> SECTION = EnumProperty.create("section", SixSection.class);

    public static final VoxelShape SHAPE_WHOLE = Block.box(-15.0D, 1.0D, 4.0D, 31.0D, 31.0D, 16.0D);

    public AirConditionerBlock(Properties properties) {
        super(properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(6.5F, 9.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SECTION, SixSection.BOTTOM_MIDDLE));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SECTION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection();
        if (blockpos.getY() < level.getMaxBuildHeight() - 2) {
            for (var sect : SixSection.BOTTOM_MIDDLE.getOtherValues()) {
                if (!level.getBlockState(SixSection.BOTTOM_MIDDLE.getRelative(blockpos, direction.getOpposite(), sect)).canBeReplaced(context))
                    return null;
            }

            return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(SECTION, SixSection.BOTTOM_MIDDLE);
        } else {
            return null;
        }
    }

    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos blockPos) {
        double x = 0.0D;
        double z = 0.0D;

        VoxelShape shape = AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), SHAPE_WHOLE);
        switch (state.getValue(FACING)) {
            case NORTH -> x = 1.0D;
            case EAST -> z = 1.0D;
            case SOUTH -> x = -1.0D;
            case WEST -> z = -1.0D;
        }

        switch (state.getValue(SECTION)) {
            case BOTTOM_LEFT -> { return shape.move(-x, 0.0D, -z); }
            case TOP_LEFT -> { return shape.move(-x, -1.0D, -z); }

            case BOTTOM_MIDDLE -> { return shape.move(0, 0.0D, 0); }
            case TOP_MIDDLE -> { return shape.move(0, -1.0D, 0); }

            case BOTTOM_RIGHT -> { return shape.move(x, 0.0D, z); }
            case TOP_RIGHT -> { return shape.move(x, -1.0D, z); }
        }

        return shape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState otherState, boolean simulate) {
        super.onPlace(state, level, pos, otherState, simulate);

        level.scheduleTick(pos, this, 4);
    }

    public boolean maybeReplaceWithFreshAir(Level level, BlockPos pos) {
        var blockState = level.getBlockState(pos);
        var fluidState = blockState.getFluidState();

        if (blockState.isAir() || blockState.is(ChangedTags.Blocks.GAS)) {
            level.setBlockAndUpdate(pos, ChangedBlocks.FRESH_AIR.get().defaultBlockState());
            return true;
        }

        else if (fluidState.getAmount() >= 7)
            return false;

        else if (blockState.isCollisionShapeFullBlock(level, pos))
            return false;

        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return state.getValue(SECTION) == SixSection.BOTTOM_MIDDLE ?
                new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance())) :
                List.of();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        super.setPlacedBy(level, pos, state, entity, item);
        var thisSect = state.getValue(SECTION);
        for (var sect : thisSect.getOtherValues())
            level.setBlockAndUpdate(thisSect.getRelative(pos, state.getValue(FACING), sect), state.setValue(SECTION, sect));
    }

    protected BlockState getBlockState(BlockState state, LevelReader level, BlockPos pos, SixSection otherSect) {
        if (state.getValue(SECTION) == otherSect)
            return state;
        return level.getBlockState(state.getValue(SECTION).getRelative(pos, state.getValue(FACING), otherSect));
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, Either<Boolean, Direction.Axis> allCheckOrAxis) {
        if (allCheckOrAxis.left().isPresent() && !allCheckOrAxis.left().get() && state.getValue(SECTION) == SixSection.BOTTOM_MIDDLE)
            return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()))
                    .isFaceSturdy(level, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));

        var thisSect = state.getValue(SECTION);
        for (var sect : allCheckOrAxis.left().isPresent() && allCheckOrAxis.left().get() ? Arrays.stream(SixSection.values()).toList() : thisSect.getOtherValues()) {
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
        if (section != SixSection.BOTTOM_MIDDLE) {
            BlockPos blockpos = section.getRelative(pos, state.getValue(FACING), SixSection.BOTTOM_MIDDLE);
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(SECTION) == SixSection.BOTTOM_MIDDLE) {
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
            } else if (state.getValue(SECTION) != SixSection.BOTTOM_MIDDLE) {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return super.rotate(state, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return super.mirror(state, mirror);
        else {
            return super.mirror(state, mirror).setValue(SECTION, state.getValue(SECTION).getHorizontalNeighbor());
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        level.scheduleTick(pos, this, 4);

        if (maybeReplaceWithFreshAir(level, pos.relative(state.getValue(FACING))))
            maybeReplaceWithFreshAir(level, pos.relative(state.getValue(FACING), 2));
    }
}
