package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LatexWolfCrystalBlock extends AbstractLatexIceBlock {

    public LatexWolfCrystalBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {

        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        if (plant.getBlock() instanceof LatexCrystal)
            return true;
        else
            return false;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);
        if (random.nextInt(200) > 0)
            return;

        BlockPos above = position.above();
        if (level.getBlockState(above).is(Blocks.AIR)) {
            level.setBlock(above, ChangedBlocks.LATEX_WOLF_CRYSTAL_SMALL.get().defaultBlockState(), 3);
        }
    }
}
