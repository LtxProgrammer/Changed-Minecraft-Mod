package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Map;

public class PipeBlock extends Block {
    public static enum ConnectState implements StringRepresentable {
        AIR("air"), PIPE("pipe"), WALL("wall");

        private final String name;

        ConnectState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public boolean isWall() {
            return this == WALL;
        }

        public boolean isPipe() {
            return this == PIPE;
        }

        public boolean isAir() {
            return this == AIR;
        }
    }

    public static final EnumProperty<ConnectState> NORTH = EnumProperty.create("north", ConnectState.class);
    public static final EnumProperty<ConnectState> EAST = EnumProperty.create("east", ConnectState.class);
    public static final EnumProperty<ConnectState> SOUTH = EnumProperty.create("south", ConnectState.class);
    public static final EnumProperty<ConnectState> WEST = EnumProperty.create("west", ConnectState.class);
    public static final Map<Direction, EnumProperty<ConnectState>> BY_DIRECTION = Map.of(
            Direction.NORTH, NORTH, Direction.SOUTH, SOUTH, Direction.EAST, EAST, Direction.WEST, WEST);

    public static VoxelShape box2d(double xMin, double zMin, double xMax, double zMax) {
        return Block.box(xMin, 9, zMin, xMax, 12, zMax);
    }

    private final VoxelShape PIPE_0 = Shapes.or(
            box2d(1, 0, 4, 3),
            box2d(12, 0, 15, 3),
            box2d(1, 3, 15, 6)
    );
    private final VoxelShape PIPE_CENTER = box2d(0, 3, 16, 6);
    private final VoxelShape PIPE_CORNER = Shapes.or(
            box2d(0, 3, 13, 6),
            box2d(10, 6, 13, 16)
    );
    private final VoxelShape PIPE_CORNER_END_1 = Shapes.or(
            box2d(1, 0, 4, 3),
            box2d(1, 3, 13, 6),
            box2d(10, 6, 13, 16)
    );
    private final VoxelShape PIPE_CORNER_END_2 = Shapes.or(
            box2d(12, 0, 15, 3),
            box2d(3, 3, 15, 6),
            box2d(3, 6, 6, 16)
    );
    private final VoxelShape PIPE_ENDING_1 = Shapes.or(
            box2d(12, 0, 15, 3),
            box2d(0, 3, 15, 6)
    );
    private final VoxelShape PIPE_ENDING_2 = Shapes.or(
            box2d(1, 0, 4, 3),
            box2d(1, 3, 16, 6)
    );
    private final VoxelShape PIPE_OUTER_CORNER = Shapes.or(
            box2d(10, 0, 13, 6),
            box2d(13, 3, 16, 6)
    );

    private final Map<BlockState, VoxelShape> shapesCache;

    public PipeBlock() {
        super(Properties.of(Material.METAL, MaterialColor.WOOL).sound(SoundType.COPPER).strength(3.0f, 3.0f));
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, ConnectState.AIR).setValue(EAST, ConnectState.AIR)
                .setValue(SOUTH, ConnectState.AIR).setValue(WEST, ConnectState.AIR));

        this.shapesCache = this.getShapeForEachState(this::calculateShape);
    }

    private VoxelShape rotateShape(Rotation rotation, VoxelShape shape) {
        return AbstractCustomShapeBlock.calculateShapesRaw(rotation, shape);
    }

    private VoxelShape calculateShape(BlockState state) {
        var north = state.getValue(NORTH);
        var south = state.getValue(SOUTH);
        var east = state.getValue(EAST);
        var west = state.getValue(WEST);

        if (north.isWall() && south.isAir() && east.isAir() && west.isAir()) return PIPE_0;
        if (north.isAir() && south.isAir() && east.isWall() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_0);
        if (north.isAir() && south.isWall() && east.isAir() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_0);
        if (north.isAir() && south.isAir() && east.isAir() && west.isWall()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_0);

        if (north.isWall() && south.isAir() && east.isPipe() && west.isPipe()) return PIPE_CENTER;
        if (north.isPipe() && south.isPipe() && east.isWall() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_CENTER);
        if (north.isAir() && south.isWall() && east.isPipe() && west.isPipe()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_CENTER);
        if (north.isPipe() && south.isPipe() && east.isAir() && west.isWall()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_CENTER);

        if (north.isWall() && south.isPipe() && east.isWall() && west.isPipe()) return PIPE_CORNER;
        if (north.isPipe() && south.isWall() && east.isWall() && west.isPipe()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_CORNER);
        if (north.isPipe() && south.isWall() && east.isPipe() && west.isWall()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_CORNER);
        if (north.isWall() && south.isPipe() && east.isPipe() && west.isWall()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_CORNER);

        if (north.isWall() && south.isPipe() && east.isWall() && west.isAir()) return PIPE_CORNER_END_1;
        if (north.isAir() && south.isWall() && east.isWall() && west.isPipe()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_CORNER_END_1);
        if (north.isPipe() && south.isWall() && east.isAir() && west.isWall()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_CORNER_END_1);
        if (north.isWall() && south.isAir() && east.isPipe() && west.isWall()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_CORNER_END_1);

        if (north.isWall() && south.isPipe() && east.isAir() && west.isWall()) return PIPE_CORNER_END_2;
        if (north.isWall() && south.isAir() && east.isWall() && west.isPipe()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_CORNER_END_2);
        if (north.isPipe() && south.isWall() && east.isWall() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_CORNER_END_2);
        if (north.isAir() && south.isWall() && east.isPipe() && west.isWall()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_CORNER_END_2);

        if (north.isWall() && south.isAir() && east.isAir() && west.isPipe()) return PIPE_ENDING_1;
        if (north.isPipe() && south.isAir() && east.isWall() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_ENDING_1);
        if (north.isAir() && south.isWall() && east.isPipe() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_ENDING_1);
        if (north.isAir() && south.isPipe() && east.isAir() && west.isWall()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_ENDING_1);

        if (north.isWall() && south.isAir() && east.isPipe() && west.isAir()) return PIPE_ENDING_2;
        if (north.isAir() && south.isPipe() && east.isWall() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_ENDING_2);
        if (north.isAir() && south.isWall() && east.isAir() && west.isPipe()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_ENDING_2);
        if (north.isPipe() && south.isAir() && east.isAir() && west.isWall()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_ENDING_2);

        if (!north.isAir() && south.isAir() && !east.isAir() && west.isAir()) return PIPE_OUTER_CORNER;
        if (north.isAir() && !south.isAir() && !east.isAir() && west.isAir()) return rotateShape(Rotation.CLOCKWISE_90, PIPE_OUTER_CORNER);
        if (north.isAir() && !south.isAir() && east.isAir() && !west.isAir()) return rotateShape(Rotation.CLOCKWISE_180, PIPE_OUTER_CORNER);
        if (!north.isAir() && south.isAir() && east.isAir() && !west.isAir()) return rotateShape(Rotation.COUNTERCLOCKWISE_90, PIPE_OUTER_CORNER);

        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return shapesCache.get(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    public final ConnectState connectsTo(BlockGetter level, BlockState state, BlockPos blockPos) {
        return state.is(this) ? ConnectState.PIPE : (state.isCollisionShapeFullBlock(level, blockPos) && !state.is(ChangedTags.Blocks.PIPE_IGNORED) ? ConnectState.WALL : ConnectState.AIR);
    }

    private BlockState stateFor(BlockState start, ConnectState north, ConnectState east, ConnectState south, ConnectState west) {
        return start.setValue(NORTH, north).setValue(EAST, east).setValue(SOUTH, south).setValue(WEST, west);
    }

    private BlockState calculateState(LevelReader level, BlockPos blockPos, BlockState state) {
        return stateFor(state,
                connectsTo(level, level.getBlockState(blockPos.north()), blockPos.north()),
                connectsTo(level, level.getBlockState(blockPos.east()), blockPos.east()),
                connectsTo(level, level.getBlockState(blockPos.south()), blockPos.south()),
                connectsTo(level, level.getBlockState(blockPos.west()), blockPos.west()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var nState = calculateState(context.getLevel(), context.getClickedPos(), super.getStateForPlacement(context));
        return isStateValid(nState) && isPosValidForNeighbors(context.getLevel(), context.getClickedPos()) ? nState : null;
    }

    public BlockState adjustNeighborForNewState(BlockState neighbor, Direction direction) {
        if (neighbor.is(this))
            return neighbor.setValue(BY_DIRECTION.get(direction.getOpposite()), ConnectState.PIPE);
        return neighbor;
    }

    public BlockState adjustNeighborForNewState(LevelReader level, BlockPos blockPos, Direction direction) {
        return adjustNeighborForNewState(level.getBlockState(blockPos.relative(direction)), direction);
    }

    public boolean isPosValidForNeighbors(LevelReader level, BlockPos blockPos) {
        return Direction.Plane.HORIZONTAL.stream().allMatch(direction -> isStateValid(adjustNeighborForNewState(level, blockPos, Direction.NORTH)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        var sState = super.updateShape(state, direction, otherState, level, pos, otherPos);
        if (direction.getAxis().isVertical())
            return sState;

        var nState = sState.setValue(BY_DIRECTION.get(direction), connectsTo(level, otherState, otherPos));

        if (!isStateValid(nState)/* || !isPosValidForNeighbors(level, pos)*/) {
            Changed.LOGGER.debug("Bad state {}, neighbor blocks [n: {}, e: {}, s: {}, w: {}]", nState,
                    level.getBlockState(pos.north()), level.getBlockState(pos.east()), level.getBlockState(pos.south()), level.getBlockState(pos.west()));

            level.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        return nState;
    }

    public boolean isStateValid(BlockState state) {
        if (shapesCache.containsKey(state) && !shapesCache.get(state).isEmpty())
            return true;
        if (state.is(this))
            return false;
        return true;
    }

    public BlockState throwIfInvalid(BlockState state) {
        if (!isStateValid(state))
            throw new IllegalArgumentException("BlockState is invalid, " + state.toString());
        return state;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return List.of(new ItemStack(this));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return throwIfInvalid(switch (mirror) {
            case NONE -> state;
            case FRONT_BACK -> state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case LEFT_RIGHT -> state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
        });
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return throwIfInvalid(switch (rotation) {
            case NONE -> state;
            case CLOCKWISE_90 -> state
                    .setValue(NORTH, state.getValue(WEST))
                    .setValue(EAST, state.getValue(NORTH))
                    .setValue(SOUTH, state.getValue(EAST))
                    .setValue(WEST, state.getValue(SOUTH));
            case CLOCKWISE_180 -> state
                    .setValue(NORTH, state.getValue(SOUTH))
                    .setValue(EAST, state.getValue(WEST))
                    .setValue(SOUTH, state.getValue(NORTH))
                    .setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90 -> state
                    .setValue(NORTH, state.getValue(EAST))
                    .setValue(EAST, state.getValue(SOUTH))
                    .setValue(SOUTH, state.getValue(WEST))
                    .setValue(WEST, state.getValue(NORTH));
        });
    }
}
