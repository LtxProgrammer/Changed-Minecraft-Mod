package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin extends Block implements BucketPickup {
    public LiquidBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback) {
        if (!(state.getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid))
            return;

        callback.setReturnValue(abstractLatexFluid.getCollisionShape(state.getFluidState(), level, blockPos, context));
    }

    public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        if (state.getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid)
            return abstractLatexFluid.isScaffolding(state.getFluidState(), level, pos, entity);
        return super.isScaffolding(state, level, pos, entity);
    }
}
