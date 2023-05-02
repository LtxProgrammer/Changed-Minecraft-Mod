package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VentHatchBlock extends TrapDoorBlock {
    public VentHatchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext) {
            var entity = entityCollisionContext.getEntity();
            if (entity != null) {
                if (entity.getType().is(ChangedTags.EntityTypes.PUDDING))
                    return Shapes.empty();
                return ProcessTransfur.ifPlayerLatex(Util.playerOrNull(entity), variant -> {
                    if (variant.getParent().getEntityType().is(ChangedTags.EntityTypes.PUDDING))
                        return Shapes.empty();
                    else
                        return super.getCollisionShape(state, level, blockPos, context);
                }, () -> super.getCollisionShape(state, level, blockPos, context));
            }
        }

        return super.getCollisionShape(state, level, blockPos, context);
    }
}
