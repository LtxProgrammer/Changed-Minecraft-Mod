package net.ltxprogrammer.changed.mixin.render;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin extends RenderStateShard {
    public RenderTypeMixin(String name, Runnable setupState, Runnable clearState) {
        super(name, setupState, clearState);
    }

    @Inject(method = "chunkBufferLayers", at = @At("RETURN"), cancellable = true)
    private static void chunkBufferLayers(CallbackInfoReturnable<List<RenderType>> callback) {
        var layers = new ArrayList<>(callback.getReturnValue());
        layers.add(layers.indexOf(RenderType.solid()) + 1, ChangedShaders.latexSolid());
        layers.add(layers.indexOf(RenderType.cutoutMipped()) + 1, ChangedShaders.latexCutoutMipped());
        layers.add(layers.indexOf(RenderType.cutout()) + 1, ChangedShaders.latexCutout());
        callback.setReturnValue(ImmutableList.copyOf(layers));
    }
}
