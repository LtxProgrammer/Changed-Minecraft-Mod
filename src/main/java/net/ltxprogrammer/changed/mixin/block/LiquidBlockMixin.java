package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
    @Shadow @Final public static VoxelShape STABLE_SHAPE;

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback) {
        if (!state.getFluidState().is(ChangedTags.Fluids.LATEX))
            return;

        if (context.canStandOnFluid(Blocks.WATER.defaultBlockState().getFluidState(), state.getFluidState())) {
            if (level.getFluidState(blockPos.above()).is(ChangedTags.Fluids.LATEX)) {
                callback.setReturnValue(Shapes.block());
                return;
            }

            int amount = state.getFluidState().getAmount();
            if (amount >= 8) {
                callback.setReturnValue(STABLE_SHAPE);
                return;
            }
        }

        callback.setReturnValue(Shapes.empty());
    }
}
