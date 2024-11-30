package net.ltxprogrammer.changed.mixin.render;

import com.mojang.math.Transformation;
import net.ltxprogrammer.changed.client.AbilityRenderer;
import net.ltxprogrammer.changed.client.BakeryExtender;
import net.ltxprogrammer.changed.client.LatexCoveredBlockRenderer;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin implements BakeryExtender {
    @Shadow @Final private static Logger LOGGER;
    @Shadow protected abstract void cacheAndQueueDependencies(ResourceLocation p_119353_, UnbakedModel p_119354_);

    @Shadow @Final private Map<Triple<ResourceLocation, Transformation, Boolean>, BakedModel> bakedCache;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Shadow public abstract UnbakedModel getModel(ResourceLocation p_119342_);
    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation p_119365_) throws IOException;

    @Inject(method = "processLoading", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
            ordinal = 3))
    private void processChangedModels(ProfilerFiller profiler, int p_119250_, CallbackInfo ci) {
        profiler.popPush("changed:abilities");

        for(ResourceLocation resourcelocation : ChangedRegistry.ABILITY.get().getKeys()) {
            if (AbilityRenderer.IGNORED.contains(resourcelocation))
                continue;

            ModelResourceLocation key = new ModelResourceLocation(resourcelocation, "ability");

            // ModernFix replaces loadTopLevel with their own function that doesn't account for abilities
            UnbakedModel unbakedmodel = this.getModel(key);
            this.unbakedCache.put(key, unbakedmodel);
            this.topLevelModels.put(key, unbakedmodel);
        }
    }

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void loadModel(ResourceLocation name, CallbackInfo callback) throws IOException {
        var model = LatexCoveredBlockRenderer.getCachedModel(name);
        if (model != null) {
            this.cacheAndQueueDependencies(name, model);
            callback.cancel();
        }

        if (name instanceof ModelResourceLocation modelResourceLocation) {
            if (Objects.equals(modelResourceLocation.getVariant(), "ability")) {
                ResourceLocation location = new ResourceLocation(name.getNamespace(), "ability/" + name.getPath());
                BlockModel blockmodel = this.loadBlockModel(location);
                this.cacheAndQueueDependencies(modelResourceLocation, blockmodel);
                this.unbakedCache.put(location, blockmodel);
                callback.cancel();
            }
        }
    }

    @Override
    @Unique
    public void removeFromCacheIf(Predicate<Triple<ResourceLocation, Transformation, Boolean>> predicate) {
        var toRemove = bakedCache.keySet().stream().filter(predicate).toList();
        for (var triple : toRemove)
            bakedCache.remove(triple);
    }
}
