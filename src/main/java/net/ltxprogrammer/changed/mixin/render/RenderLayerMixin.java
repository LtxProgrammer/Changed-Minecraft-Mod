package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.renderer.layers.PlayerLayerWrapper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin<T extends Entity, M extends EntityModel<T>> {
    @SuppressWarnings("unchecked")
    @Inject(method = "getParentModel", at = @At("HEAD"), cancellable = true)
    public void maybeOverrideModel(CallbackInfoReturnable<M> cir) {
        PlayerLayerWrapper.getOverrideLayer().ifPresent(model -> {
            cir.setReturnValue((M)model);
        });
    }
}
