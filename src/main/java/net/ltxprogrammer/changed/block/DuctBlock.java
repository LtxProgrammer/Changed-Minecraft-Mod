package net.ltxprogrammer.changed.block;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.Block;
import net.ltxprogrammer.changed.init.ChangedTags;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.HashMap;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import java.util.Map;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class DuctBlock extends ChangedBlock
{
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty VENTED = BooleanProperty.create("vented");
    public static final BooleanProperty[] FACES = { NORTH, EAST, SOUTH, WEST, UP, DOWN };
    public static final Map<Direction, BooleanProperty> BY_DIRECTION = Map.of(
            Direction.UP, UP, Direction.DOWN, DOWN, Direction.NORTH, NORTH, Direction.SOUTH, SOUTH, Direction.EAST, EAST, Direction.WEST, WEST);
    private static final VoxelShape SHAPE_FRAME = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 2.0),
            Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 2.0),
            Block.box(0.0, 0.0, 14.0, 2.0, 16.0, 16.0),
            Block.box(14.0, 0.0, 14.0, 16.0, 16.0, 16.0),

            Block.box(2.0, 0.0, 0.0, 14.0, 2.0, 2.0),
            Block.box(2.0, 14.0, 0.0, 14.0, 16.0, 2.0),
            Block.box(2.0, 0.0, 14.0, 14.0, 2.0, 16.0),
            Block.box(2.0, 14.0, 14.0, 14.0, 16.0, 16.0),

            Block.box(0.0, 0.0, 2.0, 2.0, 2.0, 14.0),
            Block.box(0.0, 14.0, 2.0, 2.0, 16.0, 14.0),
            Block.box(14.0, 0.0, 2.0, 16.0, 2.0, 14.0),
            Block.box(14.0, 14.0, 2.0, 16.0, 16.0, 14.0));
    private static final Map<Direction.Axis, VoxelShape> SHAPE_DUCT = Map.of(
            Direction.Axis.Y, Shapes.or(
                    Block.box(2.0, 0.0, 2.0, 2.02, 16.0, 14.0),
                    Block.box(2.0, 0.0, 13.98, 14.0, 16.0, 14.0),
                    Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 2.02),
                    Block.box(13.98, 0.0, 2.0, 14.0, 16.0, 14.0)),
            Direction.Axis.X, Shapes.or(
                    Block.box(0.0, 2.0, 2.0, 16.0, 14.0, 2.02),
                    Block.box(0.0, 2.0, 13.98, 16.0, 14.0, 14.0),
                    Block.box(0.0, 2.0, 2.0, 16.0, 2.02, 14.0),
                    Block.box(0.0, 13.98, 2.0, 16.0, 14.0, 14.0)),
            Direction.Axis.Z, Shapes.or(
                    Block.box(2.0, 2.0, 0.0, 2.02, 14.0, 16.0),
                    Block.box(13.98, 2.0, 0.0, 14.0, 14.0, 16.0),
                    Block.box(2.0, 2.0, 0.0, 14.0, 2.02, 16.0),
                    Block.box(2.0, 13.98, 0.0, 14.0, 14.0, 16.0)));
    private static final Map<Direction, VoxelShape> SHAPE_DUCT_PANEL = Map.of(
            Direction.UP, Block.box(2.0, 14.0, 2.0, 14.0, 16.0, 14.0),
            Direction.DOWN, Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Direction.NORTH, Block.box(2.0, 2.0, 0.0, 14.0, 14.0, 2.0),
            Direction.SOUTH, Block.box(2.0, 2.0, 14.0, 14.0, 14.0, 16.0),
            Direction.EAST, Block.box(14.0, 2.0, 2.0, 16.0, 14.0, 14.0),
            Direction.WEST, Block.box(0.0, 2.0, 2.0, 2.0, 14.0, 14.0));
    private final Map<BlockState, VoxelShape> COMPUTED_SHAPES = new HashMap<>();

    public DuctBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(VENTED, false));
        this.getStateDefinition().getPossibleStates().forEach(state -> this.COMPUTED_SHAPES.computeIfAbsent(state, this::computeShape));
    }

    protected static Optional<Direction.Axis> nonJointedAxis(final BlockState blockState) {
        Optional<Direction.Axis> candidate = Optional.empty();
        for (final Direction.Axis axis : Direction.Axis.values()) {
            if (blockState.getValue(BY_DIRECTION.get(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE))) &&
                    blockState.getValue(BY_DIRECTION.get(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE))))
                candidate = Optional.of(axis);
        }

        for (final Direction dir : Direction.values()) {
            if (blockState.getValue(BY_DIRECTION.get(dir)) && candidate.isPresent() && dir.getAxis() != candidate.get())
                return Optional.empty();
        }

        return candidate;
    }

    protected BlockState getWantedState(final BlockState current, final BlockPos blockPos, final BlockGetter level) {
        final AtomicReference<BlockState> wanted = new AtomicReference<>(current);
        for (final Direction direction : Direction.values()) {
            if (level.getBlockState(blockPos.relative(direction)).is(ChangedTags.Blocks.DUCT_CONNECTOR)) {
                wanted.set(wanted.get().setValue(BY_DIRECTION.get(direction), true));
            }
        }
        nonJointedAxis(wanted.get()).ifPresent(axis -> {
            try {
                BlockState positive = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)));
                BlockState negative = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE)));
                if (positive.is(this) && negative.is(this))
                    if (!positive.getValue(VENTED) && !negative.getValue(VENTED))
                        wanted.set(wanted.get().setValue(VENTED, true));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return wanted.get();
    }

    @NotNull
    public VoxelShape getCollisionShape(final BlockState blockState, final BlockGetter level, final BlockPos blockPos, final CollisionContext context) {
        final VoxelShape shape = this.COMPUTED_SHAPES.get(blockState);
        if (shape == null) {
            throw new IllegalStateException("Undefined state shape");
        }
        return shape;
    }

    @NotNull
    public VoxelShape getInteractionShape(final BlockState blockState, final BlockGetter level, final BlockPos blockPos) {
        final VoxelShape shape = this.COMPUTED_SHAPES.get(blockState);
        if (shape == null)
            throw new IllegalStateException("Undefined state shape");
        return shape;
    }

    @NotNull
    public VoxelShape getShape(final BlockState blockState, final BlockGetter level, final BlockPos blockPos, final CollisionContext context) {
        final VoxelShape shape = this.COMPUTED_SHAPES.get(blockState);
        if (shape == null)
            throw new IllegalStateException("Undefined state shape");
        return shape;
    }

    public VoxelShape computeShape(final BlockState blockState) {
        final Optional<Direction.Axis> opt = nonJointedAxis(blockState);
        if (opt.isEmpty()) {
            VoxelShape shape = SHAPE_FRAME;
            for (var dir : Direction.values())
                if (!blockState.getValue(BY_DIRECTION.get(dir)))
                    shape = Shapes.or(shape, SHAPE_DUCT_PANEL.get(dir));
            return shape;
        }
        return SHAPE_DUCT.get(opt.get());
    }

    public boolean isLadder(final BlockState state, final LevelReader level, final BlockPos pos, final LivingEntity entity) {
        BlockPos entityBlockPos = new BlockPos(entity.position().add(0.0, 0.25, 0.0));
        return super.isLadder(state, level, pos, entity) || entityBlockPos.equals(pos) || entity.eyeBlockPosition().equals(pos);
    }

    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.getWantedState(super.getStateForPlacement(context), context.getClickedPos(), (BlockGetter)context.getLevel());
    }

    public BlockState updateShape(final BlockState blockState, final Direction direction, final BlockState blockStateOther, final LevelAccessor level, final BlockPos blockPos, final BlockPos blockPosOther) {
        BlockState wanted = blockState.setValue(BY_DIRECTION.get(direction), blockStateOther.is(ChangedTags.Blocks.DUCT_CONNECTOR));
        final Optional<Direction.Axis> opt = nonJointedAxis(wanted);
        if (opt.isPresent()) {
            final BlockState positive = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(opt.get(), Direction.AxisDirection.POSITIVE)));
            final BlockState negative = level.getBlockState(blockPos.relative(Direction.fromAxisAndDirection(opt.get(), Direction.AxisDirection.NEGATIVE)));
            if (!positive.is(this) || !negative.is(this)) {
                return wanted;
            }
            if (!positive.getValue(VENTED) && !negative.getValue(VENTED)) {
                wanted = wanted.setValue(VENTED, true);
            }
        }
        return wanted;
    }

    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, VENTED);
    }
}