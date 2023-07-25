package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LatexWolfCrystalBlock extends AbstractLatexIceBlock {

    public LatexWolfCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {

        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        if (plant.getBlock() instanceof LatexWolfCrystal)
            return true;
        else
            return false;
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos position, @NotNull BlockState blockState, @NotNull Entity entity) {
        triggerCrystal(blockState, level, position, entity);
        super.stepOn(level, position, blockState, entity);
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        BlockPos above = pos.above();
        if (level.getBlockState(above).is(Blocks.AIR)) {
            level.setBlock(above, ChangedBlocks.LATEX_WOLF_CRYSTAL_SMALL.get().defaultBlockState(), 3);
            level.playSound(null, pos, ChangedSounds.ICE2, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private void triggerCrystal(BlockState blockState, Level level, BlockPos position, Entity entity) {

        if (entity instanceof LivingEntity le && !(entity instanceof LatexEntity) && !le.isDeadOrDying()) {
            if (entity instanceof Player player && (ProcessTransfur.isPlayerLatex(player) || player.isCreative()))
                return;
        level.scheduleTick(position, this, 20);
        }
    }
}