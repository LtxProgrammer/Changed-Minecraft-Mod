package net.ltxprogrammer.changed.mixin.render;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.ltxprogrammer.changed.client.WaveVisionRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin extends RenderStateShard {
    public RenderTypeMixin(String name, Runnable setupState, Runnable clearState) {
        super(name, setupState, clearState);
    }

    @WrapMethod(method = "chunkBufferLayers")
    private static List<RenderType> appendChunkBufferLayers(Operation<List<RenderType>> original) {
        var layers = new ArrayList<>(original.call());
        layers.add(layers.indexOf(RenderType.solid()) + 1, ChangedShaders.waveVisionResonantSolidFixed());
        layers.add(layers.indexOf(RenderType.cutoutMipped()) + 1, ChangedShaders.waveVisionResonantCutoutMippedFixed());
        layers.add(layers.indexOf(RenderType.cutout()) + 1, ChangedShaders.waveVisionResonantCutoutFixed());
        return ImmutableList.copyOf(layers);
    }
}
