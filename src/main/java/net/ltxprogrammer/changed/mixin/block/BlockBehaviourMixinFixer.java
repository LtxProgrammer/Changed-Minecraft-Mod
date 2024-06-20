package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBehaviour.class, priority = Integer.MAX_VALUE)
public class BlockBehaviourMixinFixer {
    @Inject(method = "getCollisionShape", at = @At("RETURN"), cancellable = true)
    public void getNonNullCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context,
                                         CallbackInfoReturnable<VoxelShape> callback) {
        if (callback.getReturnValue() == null) {
            callback.setReturnValue(Shapes.empty());
            Changed.LOGGER.debug("Crash prevented from null shape.");
        }
    }
}
