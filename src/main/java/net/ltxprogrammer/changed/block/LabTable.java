package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class LabTable extends ConnectedRectangleBlock {
    public static final Map<Integer, VoxelShape> TABLE_SHAPE_MAP = new HashMap<>();
    public static final VoxelShape SHAPE_TOP = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public LabTable(Properties properties) {
        super(properties);
        STATE.getPossibleValues().forEach(state -> {
            VoxelShape builder = SHAPE_TOP;
            if (hasNorthWestCorner(state))
                builder = Shapes.or(builder, Block.box(2.0D, 0.0D, 2.0D, 4.0D, 14.0D, 4.0D));
            if (hasNorthEastCorner(state))
                builder = Shapes.or(builder, Block.box(12.0D, 0.0D, 2.0D, 14.0D, 14.0D, 4.0D));
            if (hasSouthWestCorner(state))
                builder = Shapes.or(builder, Block.box(2.0D, 0.0D, 12.0D, 4.0D, 14.0D, 14.0D));
            if (hasSouthEastCorner(state))
                builder = Shapes.or(builder, Block.box(12.0D, 0.0D, 12.0D, 14.0D, 14.0D, 14.0D));
            TABLE_SHAPE_MAP.put(state, builder.optimize());
        });
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return TABLE_SHAPE_MAP.get(p_60547_.getValue(STATE));
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }
}
