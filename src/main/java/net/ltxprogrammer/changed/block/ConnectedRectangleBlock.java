package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

// Simpler version of `ConnectedFloorBlock` meant for strictly rectangular block structures
public class ConnectedRectangleBlock extends LabBlock {
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 15);
    private int getRotateC90(int state) {
        return switch (state) {
            case 1 -> 4;
            case 2 -> 8;
            case 3 -> 12;
            case 4 -> 3;
            case 5 -> 7;
            case 6 -> 11;
            case 7 -> 15;
            case 8 -> 2;
            case 9 -> 6;
            case 11 -> 14;
            case 12 -> 1;
            case 13 -> 5;
            case 14 -> 9;
            case 15 -> 13;
            default -> state;
        };
    }

    private int getRotate180(int state) {
        return getRotateC90(getRotateC90(state));
    }

    private int getRotateCC90(int state) {
        return getRotate180(getRotateC90(state));
    }

    private int calculateRotate(Rotation rotation, int state) {
        if (rotation == Rotation.NONE)
            return state;

        return switch (rotation) {
            case CLOCKWISE_90 -> getRotateC90(state);
            case CLOCKWISE_180 -> getRotate180(state);
            case COUNTERCLOCKWISE_90 -> getRotateCC90(state);
            default -> state;
        };
    }

    private int getMirrorAcrossX(int state) {
        return switch (state) {
            case 4 -> 12;
            case 5 -> 13;
            case 6 -> 14;
            case 7 -> 15;
            case 12 -> 4;
            case 13 -> 5;
            case 14 -> 6;
            case 15 -> 7;
            default -> state;
        };
    }

    private int getMirrorAcrossY(int state) {
        return getRotateCC90(getMirrorAcrossX(getRotateC90(state)));
    }

    private int calculateMirror(Mirror mirror, int state) {
        return switch (mirror) {
            case FRONT_BACK -> getMirrorAcrossX(state);
            case LEFT_RIGHT -> getMirrorAcrossY(state);
            default -> state;
        };
    }

    private int calculateState(LevelReader level, BlockPos blockPos, BlockState blockState) {
        boolean northValid = level.getBlockState(blockPos.north()).is(blockState.getBlock());
        boolean eastValid = level.getBlockState(blockPos.east()).is(blockState.getBlock());
        boolean southValid = level.getBlockState(blockPos.south()).is(blockState.getBlock());
        boolean westValid = level.getBlockState(blockPos.west()).is(blockState.getBlock());

        if (!northValid && !eastValid && !southValid && !westValid)
            return 0;
        if (!northValid && eastValid && !southValid && !westValid)
            return 1; // East
        if (!northValid && eastValid && !southValid && westValid)
            return 2; // AxisX
        if (!northValid && !eastValid && !southValid && westValid)
            return 3; // West
        if (!northValid && !eastValid && southValid && !westValid)
            return 4; // South
        if (northValid && !eastValid && southValid && !westValid)
            return 8; // AxisY
        if (northValid && !eastValid && !southValid && !westValid)
            return 12; // North

        if (northValid && eastValid && southValid && westValid)
            return 10; // Quick exit for common type

        if (!northValid && eastValid && southValid && !westValid)
            return 5;
        if (!northValid && !eastValid && southValid && westValid)
            return 7;
        if (northValid && eastValid && !southValid && !westValid)
            return 13;
        if (northValid && !eastValid && !southValid && westValid)
            return 15;

        if (northValid && eastValid && southValid && !westValid)
            return 9;
        if (!northValid && eastValid && southValid && westValid)
            return 6;
        if (northValid && eastValid && !southValid && westValid)
            return 14;
        if (northValid && !eastValid && southValid && westValid)
            return 11;

        throw new RuntimeException(); // Somehow, a state wasn't accounted for
    }

    public boolean hasNorthEastCorner(int state) {
        return switch (state) {
            case 0, 3, 4, 7 -> true;
            default -> false;
        };
    }

    public boolean hasNorthWestCorner(int state) {
        return switch (state) {
            case 0, 1, 4, 5 -> true;
            default -> false;
        };
    }

    public boolean hasSouthEastCorner(int state) {
        return switch (state) {
            case 0, 3, 12, 15 -> true;
            default -> false;
        };
    }

    public boolean hasSouthWestCorner(int state) {
        return switch (state) {
            case 0, 1, 12, 13 -> true;
            default -> false;
        };
    }

    public ConnectedRectangleBlock(Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STATE);
    }

    private boolean connectsTo(BlockState blockState) {
        return blockState.is(this);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        return super.getStateForPlacement(context)
                .setValue(STATE, calculateState(level, blockPos, this.defaultBlockState()));
    }

    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch(rotation) {
            case NONE:
                return blockState;
            default:
                return blockState.setValue(STATE, calculateRotate(rotation, blockState.getValue(STATE)));
        }
    }

    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch(mirror) {
            case NONE:
                return blockState;
            default:
                return blockState.setValue(STATE, calculateMirror(mirror, blockState.getValue(STATE)));
        }
    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockStateOther, LevelAccessor level, BlockPos blockPos, BlockPos blockPosOther) {
        if (direction.getAxis() == Direction.Axis.Y)
            return super.updateShape(blockState, direction, blockStateOther, level, blockPos, blockPosOther);
        else
            return blockState.setValue(STATE, calculateState(level, blockPos, blockState));
    }
}
