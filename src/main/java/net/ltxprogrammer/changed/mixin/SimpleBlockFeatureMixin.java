package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.block.DoubleBlockPlace;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleBlockFeature.class)
public abstract class SimpleBlockFeatureMixin {
    @Inject(method = "place", at = @At("RETURN"), cancellable = true)
    public void place(FeaturePlaceContext<SimpleBlockConfiguration> context, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (callbackInfoReturnable.getReturnValue()) {
            BlockState blockState = context.config().toPlace().getState(context.random(), context.origin());
            if (blockState.getBlock() instanceof DoubleBlockPlace doubleBlockPlace) {
                if (!context.level().isEmptyBlock(context.origin().above())) {
                    callbackInfoReturnable.setReturnValue(false);
                }

                doubleBlockPlace.placeAt(context.level(), blockState, context.origin(), 2);
            }
        }
    }
}
