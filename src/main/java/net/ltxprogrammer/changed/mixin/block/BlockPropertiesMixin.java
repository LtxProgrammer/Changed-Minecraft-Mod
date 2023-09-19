package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Function;

@Mixin(BlockBehaviour.Properties.class)
public abstract class BlockPropertiesMixin {
    @Shadow Function<BlockState, MaterialColor> materialColor;

    @Unique
    private static LatexType getTypeOrNeutral(BlockState state) {
        @Nullable var properties = state.getValues();
        if (properties != null && properties.containsKey(AbstractLatexBlock.COVERED))
            return state.getValue(AbstractLatexBlock.COVERED);
        else {
            if (properties == null)
                Changed.LOGGER.warn("BlockState has null properties! {}", state.getBlock().getClass());

            return LatexType.NEUTRAL;
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/material/Material;Lnet/minecraft/world/level/material/MaterialColor;)V", at = @At("RETURN"))
    public void initDirectColor(Material material, MaterialColor color, CallbackInfo ci) {
        var oldFunc = materialColor;
        materialColor = blockState -> {
            var latex = getTypeOrNeutral(blockState);
            if (latex != LatexType.NEUTRAL)
                return latex.materialColor; // override color
            else
                return oldFunc.apply(blockState);
        };
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/material/Material;Ljava/util/function/Function;)V", at = @At("RETURN"))
    public void initDirectColor(Material material, Function<BlockState, MaterialColor> color, CallbackInfo ci) {
        var oldFunc = materialColor;
        materialColor = blockState -> {
            var latex = getTypeOrNeutral(blockState);
            if (latex != LatexType.NEUTRAL)
                return latex.materialColor; // override color
            else
                return oldFunc.apply(blockState);
        };
    }
}
