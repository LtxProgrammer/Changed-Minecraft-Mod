package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class RailingBlock extends AbstractCustomShapeBlock implements SimpleWaterloggedBlock {
    private static final VoxelShape POST_SHAPE = Block.box(6.0, 0.0, 12.0, 10.0, 16.0, 16.0);
    private static final VoxelShape POST_SHAPE_TALL = Block.box(6.0, 0.0, 12.0, 10.0, 24.0, 16.0);
    private static final VoxelShape SIDING_SHAPE = Shapes.or(
            Block.box(0.0, 12.0, 12.0, 6.0, 16.0, 15.0),
            Block.box(10.0, 12.0, 12.0, 16.0, 16.0, 15.0),
            POST_SHAPE);
    private static final VoxelShape SIDING_SHAPE_TALL = Shapes.or(
            Block.box(0.0, 12.0, 12.0, 6.0, 24.0, 15.0),
            Block.box(10.0, 12.0, 12.0, 16.0, 24.0, 15.0),
            POST_SHAPE_TALL);
    private static final VoxelShape CORNER_SHAPE = Shapes.or(
            Block.box(0.0, 12.0, 12.0, 12.0, 16.0, 15.0),
            Block.box(12.0, 12.0, 0.0, 15.0, 16.0, 12.0),
            POST_SHAPE.move(6.0 / 16.0, 0.0, 0.0));
    private static final VoxelShape CORNER_SHAPE_TALL = Shapes.or(
            Block.box(0.0, 12.0, 12.0, 12.0, 24.0, 15.0),
            Block.box(12.0, 12.0, 0.0, 15.0, 24.0, 12.0),
            POST_SHAPE_TALL.move(6.0 / 16.0, 0.0, 0.0));

    public enum Shape implements StringRepresentable {
        POST("post",
                List.of(Direction.EAST, Direction.WEST), Direction.WEST,
                POST_SHAPE,
                POST_SHAPE_TALL),
        SIDE("side",
                List.of(Direction.EAST, Direction.WEST), Direction.WEST,
                SIDING_SHAPE,
                SIDING_SHAPE_TALL),
        INNER_CORNER("inner_corner",
                List.of(Direction.NORTH, Direction.WEST), Direction.WEST,
                CORNER_SHAPE,
                CORNER_SHAPE_TALL),
        OUTER_CORNER("outer_corner",
                List.of(Direction.SOUTH, Direction.WEST), Direction.WEST,
                POST_SHAPE.move(-6.0 / 16.0, 0.0, 0.0),
                POST_SHAPE_TALL.move(-6.0 / 16.0, 0.0, 0.0));

        private final String serializedName;
        private final List<Direction> connections;
        private final @Nullable Direction fixedDirection;
        private final VoxelShape interactionShape;
        private final VoxelShape collisionShape;

        Shape(String serializedName, List<Direction> connections, @Nullable Direction fixedDirection, VoxelShape interactionShape, VoxelShape collisionShape) {
            this.serializedName = serializedName;
            this.connections = connections;
            this.fixedDirection = fixedDirection;
            this.interactionShape = interactionShape;
            this.collisionShape = collisionShape;
        }

        @Override
        public @NotNull String getSerializedName() {
            return serializedName;
        }

        public VoxelShape getInteractionShape() {
            return interactionShape;
        }

        public VoxelShape getCollisionShape() {
            return collisionShape;
        }

        private static Direction rotateByNorth(Direction direction, Direction facing) {
            return switch (facing) {
                case NORTH, UP, DOWN -> direction;
                case EAST -> direction.getClockWise();
                case SOUTH -> direction.getOpposite();
                case WEST -> direction.getCounterClockWise();
            };
        }

        public Stream<Direction> getConnectionDirections(Direction facing) {
            return connections.stream().map(connection -> rotateByNorth(connection, facing));
        }

        public Optional<Direction> getFixedDirection(Direction facing) {
            return fixedDirection == null ? Optional.empty() : Optional.of(rotateByNorth(fixedDirection, facing));
        }
    }

    public enum ConnectionStrength {
        NONE,
        WEAK,
        STRONG;

        public boolean isWeakerThan(ConnectionStrength strength) {
            return this.ordinal() < strength.ordinal();
        }
    }

    public record ConnectResult(boolean canConnect, Direction preferredOrientation, ConnectionStrength strength) {
        private static final ConnectResult NONE = new ConnectResult(false, null, ConnectionStrength.NONE);

        public static ConnectResult direction(Direction preferredOrientation, ConnectionStrength strength) {
            if (strength == ConnectionStrength.NONE)
                return NONE;
            return new ConnectResult(true, preferredOrientation, strength);
        }

        public static ConnectResult none() {
            return NONE;
        }

        public ConnectResult filter(ConnectionStrength strength) {
            if (this.strength.isWeakerThan(strength))
                return NONE;
            return this;
        }
    }

    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public RailingBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, Shape.POST).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE, WATERLOGGED);
    }

    ConnectResult canConnect(BlockGetter level, BlockState state, Direction direction, BlockState otherState, BlockPos otherPos) {
        if (otherState.isFaceSturdy(level, otherPos, direction.getOpposite(), SupportType.FULL)) { // Sturdy face
            return ConnectResult.direction(direction.getClockWise(), ConnectionStrength.WEAK);
        } else if (otherState.is(this)) {
            final Direction otherFacing = otherState.getValue(FACING);
            final Shape otherShape = otherState.getValue(SHAPE);

            AtomicBoolean matchesDirection = new AtomicBoolean(false);
            if (otherShape.getConnectionDirections(otherFacing)
                    .peek(checkDirection -> {
                        if (checkDirection == direction.getOpposite())
                            matchesDirection.set(true);
                    })
                    .map(otherPos::relative).map(level::getBlockState)
                    .allMatch(neighborState -> neighborState.is(this)) && !matchesDirection.getAcquire())
                return ConnectResult.none(); // other piece is already fully connected, cannot connect to this piece

            if (otherState.getValue(FACING) == state.getValue(FACING).getOpposite())
                return ConnectResult.none();
            else if (otherShape == Shape.POST || otherShape == Shape.SIDE)
                return ConnectResult.direction(otherFacing, ConnectionStrength.STRONG);
            else { // Corner piece, figure out which side is already used
                final Direction otherFixedDirection = otherShape.getFixedDirection(otherFacing).orElse(null);

                if (otherFixedDirection == direction.getOpposite())
                    return ConnectResult.direction(otherFacing, ConnectionStrength.STRONG);

                final var otherConnected = otherShape.getConnectionDirections(otherFacing)
                        .filter(checkDirection -> checkDirection != direction.getOpposite())
                        .map(dir -> Pair.of(dir, level.getBlockState(otherPos.relative(dir))))
                        .filter(pair -> pair.getSecond().is(this))
                        .map(Pair::getFirst).findAny();

                if (otherConnected.isPresent()) { // Corner is attached to another piece
                    if (otherConnected.get() == otherFixedDirection) // Other piece is attached to the fixed point
                        return ConnectResult.direction(direction.getClockWise(), ConnectionStrength.STRONG);
                    else
                        return ConnectResult.direction(direction.getCounterClockWise(), ConnectionStrength.STRONG);
                } else {
                    if (otherFacing == direction)
                        return ConnectResult.direction(direction.getClockWise(), ConnectionStrength.STRONG);
                    else if (otherFacing == direction.getCounterClockWise())
                        return ConnectResult.direction(direction.getCounterClockWise(), ConnectionStrength.STRONG);
                    else if (otherFacing == direction.getClockWise())
                        return ConnectResult.direction(direction.getClockWise(), ConnectionStrength.STRONG);
                }
            }
        }

        return ConnectResult.none();
    }

    private Optional<Direction> getPreferredStraight(ConnectResult left, ConnectResult right) {
        if (left.preferredOrientation == null || right.preferredOrientation == null)
            return Optional.empty();
        if (left.strength == ConnectionStrength.STRONG && right.strength == ConnectionStrength.STRONG)
            return left.preferredOrientation == right.preferredOrientation ? Optional.of(left.preferredOrientation) : Optional.empty();
        if (left.strength == ConnectionStrength.WEAK)
            return Optional.of(right.preferredOrientation);
        else
            return Optional.of(left.preferredOrientation);
    }

    private Optional<Direction> getPreferredInnerTurn(ConnectResult left, ConnectResult right, @NotNull Direction limit) {
        if (left.preferredOrientation == null || right.preferredOrientation == null)
            return Optional.empty();
        if (left.preferredOrientation != limit && right.preferredOrientation != limit)
            return Optional.empty();
        if (left.preferredOrientation == right.preferredOrientation.getClockWise())
            return Optional.of(left.preferredOrientation);
        else
            return Optional.empty();
    }

    private Optional<Direction> getPreferredOuterTurn(ConnectResult left, ConnectResult right, @NotNull Direction limit) {
        if (left.preferredOrientation == null || right.preferredOrientation == null)
            return Optional.empty();
        if (left.preferredOrientation != limit && right.preferredOrientation != limit)
            return Optional.empty();
        if (left.preferredOrientation == right.preferredOrientation.getCounterClockWise())
            return Optional.of(left.preferredOrientation);
        else
            return Optional.empty();
    }

    private @Nullable BlockState checkConnections(BlockState state, ConnectResult connectNorth, ConnectResult connectEast, ConnectResult connectSouth, ConnectResult connectWest) {
        // Check straight line connections
        final var straight = getPreferredStraight(connectNorth, connectSouth).or(() -> getPreferredStraight(connectEast, connectWest));
        if (straight.isPresent())
            return state.setValue(FACING, straight.get()).setValue(SHAPE, Shape.SIDE);

        final var innerTurn = getPreferredInnerTurn(connectNorth, connectEast, Direction.NORTH)
                .or(() -> getPreferredInnerTurn(connectEast, connectSouth, Direction.EAST))
                .or(() -> getPreferredInnerTurn(connectSouth, connectWest, Direction.SOUTH))
                .or(() -> getPreferredInnerTurn(connectWest, connectNorth, Direction.WEST));

        if (innerTurn.isPresent())
            return state.setValue(FACING, innerTurn.get()).setValue(SHAPE, Shape.INNER_CORNER);

        final var outerTurn = getPreferredOuterTurn(connectNorth, connectWest, Direction.NORTH)
                .or(() -> getPreferredOuterTurn(connectWest, connectSouth, Direction.WEST))
                .or(() -> getPreferredOuterTurn(connectSouth, connectEast, Direction.SOUTH))
                .or(() -> getPreferredOuterTurn(connectEast, connectNorth, Direction.EAST));

        return outerTurn.map(direction -> state.setValue(FACING, direction).setValue(SHAPE, Shape.OUTER_CORNER))
                .orElse(null);
    }

    public Optional<BlockState> checkSingle(BlockState state, ConnectResult connect, Direction direction) {
        if (!connect.canConnect)
            return Optional.empty();

        if (connect.strength == ConnectionStrength.WEAK && state.getValue(FACING).getAxis() == connect.preferredOrientation.getAxis())
            return Optional.of(state.setValue(SHAPE, Shape.OUTER_CORNER).setValue(FACING, connect.preferredOrientation.getCounterClockWise()));

        if (connect.preferredOrientation == direction.getCounterClockWise())
            return Optional.of(state.setValue(SHAPE, Shape.OUTER_CORNER).setValue(FACING, connect.preferredOrientation.getCounterClockWise()));
        else
            return Optional.of(state.setValue(SHAPE, Shape.OUTER_CORNER).setValue(FACING, connect.preferredOrientation));
    }

    public @Nullable BlockState checkSingles(BlockState state, ConnectResult connectNorth, ConnectResult connectEast, ConnectResult connectSouth, ConnectResult connectWest) {
        return checkSingle(state, connectNorth, Direction.NORTH)
                .or(() -> checkSingle(state, connectEast, Direction.EAST))
                .or(() -> checkSingle(state, connectSouth, Direction.SOUTH))
                .or(() -> checkSingle(state, connectWest, Direction.WEST)).orElse(null);
    }

    public BlockState computeState(LevelReader level, BlockPos pos, BlockState state) {
        if (state == null)
            return null;

        BlockState stateNorth = level.getBlockState(pos.north());
        BlockState stateEast = level.getBlockState(pos.east());
        BlockState stateSouth = level.getBlockState(pos.south());
        BlockState stateWest = level.getBlockState(pos.west());

        ConnectResult connectNorth = canConnect(level, state, Direction.NORTH, stateNorth, pos.north());
        ConnectResult connectEast = canConnect(level, state, Direction.EAST, stateEast, pos.east());
        ConnectResult connectSouth = canConnect(level, state, Direction.SOUTH, stateSouth, pos.south());
        ConnectResult connectWest = canConnect(level, state, Direction.WEST, stateWest, pos.west());

        if (!connectNorth.canConnect && !connectEast.canConnect && !connectSouth.canConnect && !connectWest.canConnect)
            return state.setValue(SHAPE, Shape.POST);

        BlockState strongConnection = checkConnections(state,
                connectNorth.filter(ConnectionStrength.STRONG),
                connectEast.filter(ConnectionStrength.STRONG),
                connectSouth.filter(ConnectionStrength.STRONG),
                connectWest.filter(ConnectionStrength.STRONG)
        );

        if (strongConnection != null)
            return strongConnection;

        BlockState weakConnection = checkConnections(state,
                connectNorth,
                connectEast,
                connectSouth,
                connectWest
        );

        if (weakConnection != null)
            return weakConnection;

        BlockState singleConnection = checkSingles(state,
                connectNorth,
                connectEast,
                connectSouth,
                connectWest
        );

        if (singleConnection != null)
            return singleConnection;

        return state.setValue(SHAPE, Shape.POST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return computeState(context.getLevel(), context.getClickedPos(), super.getStateForPlacement(context)).setValue(WATERLOGGED, flag);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return computeState(level, pos, super.updateShape(state, direction, otherState, level, pos, otherPos));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return calculateShapes(state.getValue(FACING), state.getValue(SHAPE).getCollisionShape());
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return calculateShapes(state.getValue(FACING), state.getValue(SHAPE).getInteractionShape());
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return calculateShapes(state.getValue(FACING), state.getValue(SHAPE).getInteractionShape());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return calculateShapes(state.getValue(FACING), state.getValue(SHAPE).getInteractionShape());
    }
}
