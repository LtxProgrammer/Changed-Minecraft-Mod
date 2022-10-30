package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin extends Block implements SimpleWaterloggedBlock {
    public WallBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    // TODO BUG: walls render weirdly
    @Inject(method = "makeShapes", at = @At("RETURN"), cancellable = true)
    private void makeShapes(float p_57966_, float p_57967_, float p_57968_, float p_57969_, float p_57970_, float p_57971_, CallbackInfoReturnable<Map<BlockState, VoxelShape>> callbackInfoReturnable) {
        if (!defaultBlockState().getProperties().contains(COVERED))
            return;

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        var prev = callbackInfoReturnable.getReturnValue();
        builder.putAll(callbackInfoReturnable.getReturnValue());
        for (var coveredProperty : COVERED.getPossibleValues()) {
            prev.forEach(((blockState, voxelShape) -> {
                if (blockState.getValue(COVERED) == coveredProperty) return;

                builder.put(blockState.setValue(COVERED, coveredProperty), voxelShape);
            }));
        }

        callbackInfoReturnable.setReturnValue(builder.build());
    }
}
