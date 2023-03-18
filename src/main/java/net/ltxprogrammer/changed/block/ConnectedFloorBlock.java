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

public class ConnectedFloorBlock extends ChangedBlock {
    // Refer to https://optifine.readthedocs.io/_images/ctm.png
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 46);
    private int getRotateC90(int state) {
        return switch (state) {
            case 1 -> 12;
            case 2 -> 24;
            case 3 -> 36;
            case 4 -> 5;
            case 5 -> 17;
            case 6 -> 7;
            case 7 -> 19;
            case 8 -> 9;
            case 9 -> 21;
            case 10 -> 11;
            case 11 -> 23;
            case 12 -> 3;
            case 13 -> 15;
            case 14 -> 27;
            case 15 -> 39;
            case 16 -> 4;
            case 17 -> 16;
            case 18 -> 6;
            case 19 -> 18;
            case 20 -> 8;
            case 21 -> 20;
            case 22 -> 10;
            case 23 -> 22;
            case 24 -> 2;
            case 25 -> 14;
            case 27 -> 38;
            case 28 -> 29;
            case 29 -> 41;
            case 30 -> 31;
            case 31 -> 43;
            case 32 -> 33;
            case 33 -> 45;
            case 34 -> 35;
            case 35 -> 34;
            case 36 -> 1;
            case 37 -> 13;
            case 38 -> 25;
            case 39 -> 37;
            case 40 -> 28;
            case 41 -> 40;
            case 42 -> 30;
            case 43 -> 41;
            case 44 -> 32;
            case 45 -> 44;
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
            case 4 -> 16;
            case 5 -> 17;
            case 7 -> 18;
            case 8 -> 9;
            case 9 -> 8;
            case 11 -> 22;
            case 12 -> 36;
            case 13 -> 37;
            case 14 -> 38;
            case 15 -> 39;
            case 16 -> 4;
            case 17 -> 5;
            case 18 -> 7;
            case 20 -> 21;
            case 21 -> 20;
            case 22 -> 11;
            case 28 -> 30;
            case 29 -> 42;
            case 30 -> 28;
            case 31 -> 40;
            case 32 -> 44;
            case 33 -> 45;
            case 34 -> 35;
            case 35 -> 34;
            case 36 -> 12;
            case 37 -> 13;
            case 38 -> 14;
            case 39 -> 15;
            case 40 -> 31;
            case 41 -> 43;
            case 42 -> 29;
            case 43 -> 41;
            case 44 -> 32;
            case 45 -> 33;
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
            return 12; // South
        if (northValid && !eastValid && southValid && !westValid)
            return 24; // AxisY
        if (northValid && !eastValid && !southValid && !westValid)
            return 36; // North

        boolean northEastValid = level.getBlockState(blockPos.north().east()).is(blockState.getBlock());
        boolean southEastValid = level.getBlockState(blockPos.south().east()).is(blockState.getBlock());
        boolean southWestValid = level.getBlockState(blockPos.south().west()).is(blockState.getBlock());
        boolean northWestValid = level.getBlockState(blockPos.north().west()).is(blockState.getBlock());

        if (northValid && eastValid && southValid && westValid &&
            northEastValid && northWestValid && southEastValid && southWestValid)
            return 26; // Quick exit for common type

        if (!northValid && eastValid && southValid && !westValid)
            return southEastValid ? 13 : 4;
        if (!northValid && !eastValid && southValid && westValid)
            return southWestValid ? 15 : 5;
        if (northValid && eastValid && !southValid && !westValid)
            return northEastValid ? 37 : 16;
        if (northValid && !eastValid && !southValid && westValid)
            return northWestValid ? 39 : 17;

        if (northValid && eastValid && southValid && !westValid)
            return northEastValid && southEastValid ? 25 : (northEastValid ? 30 : (southEastValid ? 28 : 6));
        if (!northValid && eastValid && southValid && westValid)
            return southEastValid && southWestValid ? 14 : (southEastValid ? 31 : (southWestValid ? 29 : 7));
        if (northValid && eastValid && !southValid && westValid)
            return northEastValid && northWestValid ? 38 : (northEastValid ? 40 : (northWestValid ? 42 : 18));
        if (northValid && !eastValid && southValid && westValid)
            return northWestValid && southWestValid ? 27 : (northWestValid ? 41 : (southWestValid ? 43 : 19));

        // At this point, every direct block is valid. Check corners
        if (!northEastValid && !southEastValid && !southWestValid && !northWestValid)
            return 46;

        if (northEastValid && !southWestValid && !northWestValid)
            return southEastValid ? 23 : 8;
        if (!northEastValid && southEastValid && !northWestValid)
            return southWestValid ? 22 : 9;
        if (!southEastValid && !southWestValid && northWestValid)
            return northEastValid ? 11 : 20;
        if (!northEastValid && !southEastValid && southWestValid)
            return northWestValid ? 10 : 21;

        if (!northWestValid && !southEastValid)
            return 34;
        if (!northEastValid && !southWestValid)
            return 35;

        if (!southEastValid)
            return 32;
        if (!southWestValid)
            return 33;
        if (!northEastValid)
            return 44;
        if (!northWestValid)
            return 45;

        throw new RuntimeException(); // Somehow, a state wasn't accounted for
    }

    public ConnectedFloorBlock(Properties properties) {
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

    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        switch(direction) {
            case NONE:
                return state;
            default:
                return state.setValue(STATE, calculateRotate(direction, state.getValue(STATE)));
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
