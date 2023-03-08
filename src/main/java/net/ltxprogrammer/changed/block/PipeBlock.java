package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Locale;

public class PipeBlock extends AbstractCustomShapeBlock {
    public static enum PipeState implements StringRepresentable {
        SINGLE,
        CENTER,
        END_RIGHT,
        END_LEFT,
        EXTERIOR_CORNER,
        INTERIOR_CORNER;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public PipeBlock() {
        super(Properties.of(Material.METAL, MaterialColor.WOOL));
    }

    public final boolean attachesTo(BlockState state, boolean exception) {
        return !isExceptionForConnection(state) && exception || state.getBlock() instanceof IronBarsBlock || state.is(BlockTags.WALLS);
    }
/*

    private BlockState calculateState(LevelReader level, BlockPos blockPos, BlockState blockState) {
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
*/

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        return super.updateShape(state, direction, otherState, level, pos, otherPos);
    }
}
