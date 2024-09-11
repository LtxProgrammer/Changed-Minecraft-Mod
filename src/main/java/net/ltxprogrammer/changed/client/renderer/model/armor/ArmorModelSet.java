package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ArmorModelSet<T extends ChangedEntity, A extends LatexHumanoidArmorModel<T, ?>> {
    private final Map<ArmorModel, ModelLayerLocation> modelDefinitions = new HashMap<>();
    private final Function<ArmorModel, LayerDefinition> modelCreator;
    private final BiFunction<ModelPart, ArmorModel, A> modelWrapper;

    public ArmorModelSet(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, BiFunction<ModelPart, ArmorModel, A> modelWrapper) {
        Arrays.stream(ArmorModel.values()).forEach(model -> {
            modelDefinitions.put(model, new ModelLayerLocation(rootId, model.identifier));
        });
        this.modelCreator = modelCreator;
        this.modelWrapper = modelWrapper;
    }

    public static <T extends ChangedEntity, A extends LatexHumanoidArmorModel<T, ?>> ArmorModelSet<T, A> of(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, BiFunction<ModelPart, ArmorModel, A> modelWrapper) {
        return new ArmorModelSet<>(rootId, modelCreator, modelWrapper);
    }

    public void registerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        modelDefinitions.forEach((model, id) -> consumer.accept(id, () -> modelCreator.apply(model)));
    }

    public A createModel(EntityModelSet bakery, ArmorModel model) {
        return modelWrapper.apply(bakery.bakeLayer(modelDefinitions.get(model)), model);
    }

    public Map<ArmorModel, A> createModels(EntityModelSet bakery) {
        return modelDefinitions.keySet().stream().collect(Collectors.toUnmodifiableMap(Function.identity(), armorModel -> this.createModel(bakery, armorModel)));
    }

    public ModelLayerLocation getModelName(ArmorModel model) {
        return modelDefinitions.get(model);
    }
}
