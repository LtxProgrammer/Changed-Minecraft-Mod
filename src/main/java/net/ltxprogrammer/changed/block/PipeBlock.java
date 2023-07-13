package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.HashMap;
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

    private final Map<BlockState, Boolean> STATE_VALID_CACHE = new HashMap<>();

    public PipeBlock() {
        super(Properties.of(Material.METAL, MaterialColor.WOOL).sound(SoundType.COPPER));
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, ConnectState.AIR).setValue(EAST, ConnectState.AIR)
                .setValue(SOUTH, ConnectState.AIR).setValue(WEST, ConnectState.AIR));

        this.stateDefinition.getPossibleStates().forEach(state -> {
            STATE_VALID_CACHE.computeIfAbsent(state, blockState -> {
                var north = blockState.getValue(NORTH);
                var east = blockState.getValue(EAST);
                var south = blockState.getValue(SOUTH);
                var west = blockState.getValue(WEST);

                if (north.isAir() && south.isAir()) {
                    return east.isAir() != west.isAir();
                } else if (east.isAir() && west.isAir()) {
                    return north.isAir() != south.isAir();
                }

                return true;
            });
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    public final ConnectState connectsTo(BlockGetter level, BlockState state, BlockPos blockPos) {
        return state.is(this) ? ConnectState.PIPE : (state.isCollisionShapeFullBlock(level, blockPos) ? ConnectState.WALL : ConnectState.AIR);
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
        return isStateValid(nState) ? nState : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        var nState = calculateState(level, pos, super.updateShape(state, direction, otherState, level, pos, otherPos));
        if (!isStateValid(nState)) {
            level.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        return nState;
    }

    public boolean isStateValid(BlockState state) {
        return STATE_VALID_CACHE.get(state);
    }
}
