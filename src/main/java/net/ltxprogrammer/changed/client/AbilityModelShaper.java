package net.ltxprogrammer.changed.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Map;

public class AbilityModelShaper {
    public final Object2ObjectMap<ResourceLocation, ModelResourceLocation> shapes = new Object2ObjectOpenHashMap<>(64);
    private final Object2ObjectMap<ResourceLocation, BakedModel> shapesCache = new Object2ObjectOpenHashMap<>(64);
    private final ModelManager modelManager;

    public AbilityModelShaper(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public BakedModel getAbilityModel(AbstractAbilityInstance abilityInstance) {
        BakedModel model = this.getAbilityModel(abilityInstance.ability);
        // FORGE: Make sure to call the item overrides
        return model == null ? this.modelManager.getMissingModel() : model;
    }

    @Nullable
    public BakedModel getAbilityModel(AbstractAbility<?> ability) {
        return this.shapesCache.get(ability.delegate.name());
    }

    public void register(AbstractAbility<?> ability, ModelResourceLocation modelLocation) {
        this.shapes.put(ability.delegate.name(), modelLocation);
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public void rebuildCache() {
        this.shapesCache.clear();
        this.shapes.forEach((registryName, modelName) -> this.shapesCache.put(registryName, this.modelManager.getModel(modelName)));
    }
}
