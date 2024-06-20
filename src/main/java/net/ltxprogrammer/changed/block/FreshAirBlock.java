package net.ltxprogrammer.changed.block;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.fluid.Gas;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Random;

public class FreshAirBlock extends AirBlock {
    private static final List<Vec3i> OFFSETS =
            List.of(Direction.NORTH.getNormal(),
                    Direction.EAST.getNormal(),
                    Direction.SOUTH.getNormal(),
                    Direction.WEST.getNormal(),
                    Direction.NORTH.getNormal().multiply(2),
                    Direction.EAST.getNormal().multiply(2),
                    Direction.SOUTH.getNormal().multiply(2),
                    Direction.WEST.getNormal().multiply(2));

    public FreshAirBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return !(fluid instanceof Gas);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true;
    }

    private static Direction getNearest(Vec3i normal) {
        return Direction.getNearest(normal.getX(), normal.getY(), normal.getZ());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.randomTick(state, level, pos, random);

        boolean shouldExist = OFFSETS.stream().map(pos::offset)
                .filter(level::hasChunkAt)
                .map(blockPos -> Pair.of(blockPos, level.getBlockState(blockPos)))
                .filter(pair -> pair.getSecond().is(ChangedTags.Blocks.AIR_CONDITIONER) && pair.getSecond().getProperties().contains(BlockStateProperties.HORIZONTAL_FACING))
                .map(pair -> Pair.of(getNearest(pair.getFirst().subtract(pos)), pair.getSecond().getValue(BlockStateProperties.HORIZONTAL_FACING)))
                .anyMatch(pair -> pair.getFirst() == pair.getSecond());

        if (!shouldExist) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }
}
