package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbstractPuddle extends AbstractCustomShapeBlock implements NonLatexCoverableBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    protected final LatexVariant<?> variant;

    public AbstractPuddle(Properties properties, LatexVariant<?> variant) {
        super(properties);
        this.variant = variant;
    }

    public boolean canSurvive(BlockState p_49325_, LevelReader p_49326_, BlockPos p_49327_) {
        BlockPos blockpos = p_49327_.below();
        return canSupportRigidBlock(p_49326_, blockpos);
    }

    public void entityInside(BlockState p_49314_, Level p_49315_, BlockPos p_49316_, Entity p_49317_) {
        if (!p_49315_.isClientSide && p_49317_ instanceof LivingEntity entity) {
            if (ProcessTransfur.progressTransfur(entity, 6.0f, variant.getFormId()))
                p_49315_.removeBlock(p_49316_, false);
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE_WHOLE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }
}
