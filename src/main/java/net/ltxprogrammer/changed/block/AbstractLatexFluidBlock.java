package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public abstract class AbstractLatexFluidBlock extends LiquidBlock {
    public AbstractLatexFluidBlock(Supplier<? extends FlowingFluid> flowingFluid, Properties properties) {
        super(flowingFluid, properties);
    }

    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (context.canStandOnFluid(Blocks.WATER.defaultBlockState().getFluidState(), blockState.getFluidState())) {
            if (context.isAbove(Shapes.block(), blockPos, true) && !context.isDescending() && level.getFluidState(blockPos.above()).is(ChangedTags.Fluids.LATEX)) {
                return Shapes.block();
            } else if (context.isAbove(LiquidBlock.STABLE_SHAPE, blockPos, true) && !context.isDescending() && blockState.getFluidState().getAmount() >= 8) {
                return LiquidBlock.STABLE_SHAPE;
            }
        }

        return Shapes.empty();
    }

    public boolean isScaffolding(BlockState blockState, LevelReader level, BlockPos pos, LivingEntity entity) {
        return entity.canStandOnFluid(blockState.getFluidState());
    }
}
