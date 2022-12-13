package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class AbstractPuddle extends ChangedBlock implements NonLatexCoverableBlock {
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
            if (ProcessTransfur.progressTransfur(entity, 6000, variant.getFormId()))
                p_49315_.removeBlock(p_49316_, false);
        }
    }
}
