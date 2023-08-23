package net.ltxprogrammer.changed.mixin.block;

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

import java.util.function.Function;

@Mixin(BlockBehaviour.Properties.class)
public abstract class BlockPropertiesMixin {
    @Shadow Function<BlockState, MaterialColor> materialColor;

    @Unique
    private static LatexType getTypeOrNeutral(BlockState state) {
        return state.getProperties().contains(AbstractLatexBlock.COVERED) ? state.getValue(AbstractLatexBlock.COVERED) : LatexType.NEUTRAL;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/material/Material;Lnet/minecraft/world/level/material/MaterialColor;)V", at = @At("RETURN"))
    public void initDirectColor(Material material, MaterialColor color, CallbackInfo ci) {
        materialColor = blockState -> {
            var latex = getTypeOrNeutral(blockState);
            if (latex != LatexType.NEUTRAL)
                return latex.materialColor; // override color
            else
                return color;
        };
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/material/Material;Ljava/util/function/Function;)V", at = @At("RETURN"))
    public void initDirectColor(Material material, Function<BlockState, MaterialColor> fn, CallbackInfo ci) {
        materialColor = blockState -> {
            var latex = getTypeOrNeutral(blockState);
            if (latex != LatexType.NEUTRAL)
                return latex.materialColor; // override color
            else
                return fn.apply(blockState);
        };
    }
}
