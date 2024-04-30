package net.ltxprogrammer.changed.block;

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

    enum Shape implements StringRepresentable {
        POST("post", POST_SHAPE, POST_SHAPE_TALL),
        SIDE("side", SIDING_SHAPE, SIDING_SHAPE_TALL),
        INNER_CORNER("inner_corner", CORNER_SHAPE, CORNER_SHAPE_TALL),
        OUTER_CORNER("outer_corner", POST_SHAPE.move(6.0 / 16.0, 0.0, 0.0), POST_SHAPE_TALL.move(6.0 / 16.0, 0.0, 0.0)),
        OUTER_CORNER_ALT("outer_corner_alt", POST_SHAPE.move(-6.0 / 16.0, 0.0, 0.0), POST_SHAPE_TALL.move(-6.0 / 16.0, 0.0, 0.0));

        private final String serializedName;
        private final VoxelShape interactionShape;
        private final VoxelShape collisionShape;

        Shape(String serializedName, VoxelShape interactionShape, VoxelShape collisionShape) {
            this.serializedName = serializedName;
            this.interactionShape = interactionShape;
            this.collisionShape = collisionShape;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }

        public VoxelShape getInteractionShape() {
            return interactionShape;
        }

        public VoxelShape getCollisionShape() {
            return collisionShape;
        }
    }

    record ConnectResult(boolean canConnect, Direction preferredOrientation, boolean alteredDir) {
        private static final ConnectResult NONE = new ConnectResult(false, null, false);

        public static ConnectResult direction(Direction preferredOrientation, boolean alteredDir) {
            return new ConnectResult(true, preferredOrientation, alteredDir);
        }

        public static ConnectResult none() {
            return NONE;
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
            return ConnectResult.direction(state.getValue(FACING), false);
        } else if (otherState.is(this)) {
            if (otherState.getValue(FACING) == state.getValue(FACING).getOpposite() &&
                    (otherState.getValue(SHAPE) == Shape.SIDE || state.getValue(SHAPE) == Shape.SIDE))
                return ConnectResult.none();
            else {
                return switch(otherState.getValue(SHAPE)) {
                    case POST, SIDE -> ConnectResult.direction(otherState.getValue(FACING), false);
                    case INNER_CORNER, OUTER_CORNER -> otherState.getValue(FACING).getAxis() == direction.getAxis() ?
                            ConnectResult.direction(otherState.getValue(FACING).getCounterClockWise(), true) :
                            ConnectResult.direction(otherState.getValue(FACING), false);
                    case OUTER_CORNER_ALT -> otherState.getValue(FACING).getAxis() == direction.getAxis() ?
                            ConnectResult.direction(otherState.getValue(FACING).getClockWise(), true) :
                            ConnectResult.direction(otherState.getValue(FACING), false);
                };
            }
        }

        return ConnectResult.none();
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

        // Check straight line connections
        if (!connectNorth.canConnect && !connectSouth.canConnect) {
            if (connectEast.canConnect && connectWest.canConnect) {
                if (connectEast.preferredOrientation == connectWest.preferredOrientation)
                    return state.setValue(FACING, connectEast.preferredOrientation).setValue(SHAPE, Shape.SIDE);
            }

            if (connectEast.canConnect && !connectEast.alteredDir && !connectWest.canConnect) {
                return state.setValue(FACING, connectEast.preferredOrientation).setValue(SHAPE,
                        connectEast.preferredOrientation == Direction.SOUTH ? Shape.OUTER_CORNER_ALT : Shape.OUTER_CORNER);
            } else if (!connectEast.canConnect && !connectWest.alteredDir) {
                return state.setValue(FACING, connectWest.preferredOrientation).setValue(SHAPE,
                        connectWest.preferredOrientation == Direction.SOUTH ? Shape.OUTER_CORNER : Shape.OUTER_CORNER_ALT);
            }
        } else if (!connectEast.canConnect && !connectWest.canConnect) {
            if (connectNorth.canConnect && connectSouth.canConnect) {
                if (connectNorth.preferredOrientation == connectSouth.preferredOrientation)
                    return state.setValue(FACING, connectNorth.preferredOrientation).setValue(SHAPE, Shape.SIDE);
            }

            if (connectNorth.canConnect && !connectNorth.alteredDir && !connectSouth.canConnect) {
                return state.setValue(FACING, connectNorth.preferredOrientation).setValue(SHAPE,
                        connectNorth.preferredOrientation == Direction.EAST ? Shape.OUTER_CORNER_ALT : Shape.OUTER_CORNER);
            } else if (!connectNorth.canConnect && !connectSouth.alteredDir) {
                return state.setValue(FACING, connectSouth.preferredOrientation).setValue(SHAPE,
                        connectSouth.preferredOrientation == Direction.EAST ? Shape.OUTER_CORNER : Shape.OUTER_CORNER_ALT);
            }
        }

        if (!connectEast.canConnect && !connectNorth.canConnect && connectWest.preferredOrientation != null &&
                connectSouth.preferredOrientation == connectWest.preferredOrientation.getClockWise()) {
            if (connectSouth.alteredDir && connectWest.alteredDir)
                return state.setValue(FACING, connectSouth.preferredOrientation).setValue(SHAPE, Shape.INNER_CORNER);
            else
                return state.setValue(FACING, connectSouth.preferredOrientation).setValue(SHAPE, connectSouth.preferredOrientation == Direction.WEST ? Shape.INNER_CORNER : Shape.OUTER_CORNER);
        } else if (!connectNorth.canConnect && !connectWest.canConnect && connectSouth.preferredOrientation != null &&
                connectEast.preferredOrientation == connectSouth.preferredOrientation.getClockWise()) {
            if (connectEast.alteredDir && connectSouth.alteredDir)
                return state.setValue(FACING, connectEast.preferredOrientation).setValue(SHAPE, Shape.INNER_CORNER);
            else
                return state.setValue(FACING, connectEast.preferredOrientation).setValue(SHAPE, connectEast.preferredOrientation == Direction.SOUTH ? Shape.INNER_CORNER : Shape.OUTER_CORNER);
        } else if (!connectWest.canConnect && !connectSouth.canConnect && connectEast.preferredOrientation != null &&
                connectNorth.preferredOrientation == connectEast.preferredOrientation.getClockWise()) {
            if (connectNorth.alteredDir && connectEast.alteredDir)
                return state.setValue(FACING, connectNorth.preferredOrientation).setValue(SHAPE, Shape.INNER_CORNER);
            else
                return state.setValue(FACING, connectNorth.preferredOrientation).setValue(SHAPE, connectNorth.preferredOrientation == Direction.EAST ? Shape.INNER_CORNER : Shape.OUTER_CORNER);
        } else if (!connectSouth.canConnect && !connectEast.canConnect && connectNorth.preferredOrientation != null &&
                connectWest.preferredOrientation == connectNorth.preferredOrientation.getClockWise()) {
            if (connectWest.alteredDir && connectNorth.alteredDir)
                return state.setValue(FACING, connectWest.preferredOrientation).setValue(SHAPE, Shape.INNER_CORNER);
            else
                return state.setValue(FACING, connectWest.preferredOrientation).setValue(SHAPE, connectWest.preferredOrientation == Direction.NORTH ? Shape.INNER_CORNER : Shape.OUTER_CORNER);
        }

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
