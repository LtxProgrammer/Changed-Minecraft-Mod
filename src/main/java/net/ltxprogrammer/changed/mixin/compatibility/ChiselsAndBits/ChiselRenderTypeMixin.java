package net.ltxprogrammer.changed.mixin.compatibility.ChiselsAndBits;

import mod.chiselsandbits.client.model.baked.chiseled.ChiselRenderType;
import mod.chiselsandbits.client.model.baked.chiseled.VoxelType;
import net.ltxprogrammer.changed.extension.chiselsandbits.ChangedChiselRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.IExtensibleEnum;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChiselRenderType.class, remap = false)
public abstract class ChiselRenderTypeMixin implements IExtensibleEnum {
    private static ChiselRenderType create(String name, RenderType layer, VoxelType type) {
        throw new NotImplementedException("Enum not extended");
    }

    @Unique
    private static void doNothing(ChiselRenderType type) {
        // does nothing lol
    }

    @Inject(method = "values", at = @At("HEAD"))
    private static void injectChangedTypes(CallbackInfoReturnable<ChiselRenderType[]> callback) {
        // Ensures ChangedBlockRenderPass values are loaded
        doNothing(ChangedChiselRenderTypes.LATEX_SOLID);
        doNothing(ChangedChiselRenderTypes.LATEX_SOLID_FLUID);
        doNothing(ChangedChiselRenderTypes.LATEX_CUTOUT);
        doNothing(ChangedChiselRenderTypes.LATEX_CUTOUT_MIPPED);
    }

    // Chisels and bits will crash if it attempts to convert a rendertype it's not prepared for
    @Inject(method = "fromLayer", at = @At("HEAD"), cancellable = true)
    private static void orLatexLayer(RenderType layerInfo, boolean isFluid, CallbackInfoReturnable<ChiselRenderType> cir) {
        if (ChangedChiselRenderTypes.LATEX_CUTOUT.layer.equals(layerInfo)) {
            cir.setReturnValue(ChangedChiselRenderTypes.LATEX_CUTOUT);
        } else if (ChangedChiselRenderTypes.LATEX_CUTOUT_MIPPED.layer.equals(layerInfo)) {
            cir.setReturnValue(ChangedChiselRenderTypes.LATEX_CUTOUT_MIPPED);
        } else if (ChangedChiselRenderTypes.LATEX_SOLID.layer.equals(layerInfo)) {
            cir.setReturnValue(isFluid ? ChangedChiselRenderTypes.LATEX_SOLID_FLUID : ChangedChiselRenderTypes.LATEX_SOLID);
        }
    }
}
