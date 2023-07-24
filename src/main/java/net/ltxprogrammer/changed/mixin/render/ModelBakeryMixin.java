package net.ltxprogrammer.changed.mixin.render;

import com.mojang.math.Transformation;
import net.ltxprogrammer.changed.client.BakeryExtender;
import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin implements BakeryExtender {
    @Shadow @Final private static Logger LOGGER;
    @Shadow protected abstract void cacheAndQueueDependencies(ResourceLocation p_119353_, UnbakedModel p_119354_);

    @Shadow @Final private Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache;

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void loadModel(ResourceLocation name, CallbackInfo callback) {
        var model = LatexCoveredBlocks.getCachedModel(name);
        if (model == null)
            return;
        this.cacheAndQueueDependencies(name, model);
        callback.cancel();
    }

    @Override
    @Unique
    public void removeFromCacheIf(Predicate<Triple<ResourceLocation, Transformation, Boolean>> predicate) {
        var toRemove = bakedCache.keySet().stream().filter(predicate).toList();
        for (var triple : toRemove)
            bakedCache.remove(triple);
    }
}
