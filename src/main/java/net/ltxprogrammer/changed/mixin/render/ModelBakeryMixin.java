package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow @Final private static Logger LOGGER;
    @Shadow protected abstract void cacheAndQueueDependencies(ResourceLocation p_119353_, UnbakedModel p_119354_);

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void loadModel(ResourceLocation name, CallbackInfo callback) {
        var model = LatexCoveredBlocks.getCachedModel(name);
        if (model == null)
            return;
        this.cacheAndQueueDependencies(name, model);
        callback.cancel();
    }
}
