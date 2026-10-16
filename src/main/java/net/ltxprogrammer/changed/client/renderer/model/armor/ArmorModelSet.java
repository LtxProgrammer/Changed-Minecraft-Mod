package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ArmorModelSet<T extends LivingEntity, M extends EntityModel<? super T>> {
    private final Map<ArmorModel, ModelLayerLocation> modelDefinitions = new HashMap<>();
    private final Function<ArmorModel, LayerDefinition> modelCreator;
    private final BiFunction<ModelPart, ArmorModel, M> modelWrapper;
    private final boolean slimTextures;

    private ArmorModelSet(Map<ArmorModel, ModelLayerLocation> copiedDefinitions, BiFunction<ModelPart, ArmorModel, M> modelWrapper, boolean slimTextures) {
        modelDefinitions.putAll(copiedDefinitions);
        this.modelCreator = (armorModel -> null);
        this.modelWrapper = modelWrapper;
        this.slimTextures = slimTextures;
    }

    public ArmorModelSet(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, BiFunction<ModelPart, ArmorModel, M> modelWrapper, boolean slimTextures) {
        ArmorModels.streamModels().forEach(model -> {
            modelDefinitions.put(model, new ModelLayerLocation(rootId, model.identifier));
        });
        this.modelCreator = modelCreator;
        this.modelWrapper = modelWrapper;
        this.slimTextures = slimTextures;
    }

    public boolean usesSlimTextures() {
        return slimTextures;
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> ofUnspecified(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator) {
        return ofUnspecified(rootId, modelCreator, false);
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> ofUnspecified(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, boolean slimTextures) {
        return new ArmorModelSet<>(rootId, modelCreator, ((rootPart, armorModel) -> null), slimTextures) {
            @Override
            public M createModel(EntityModelSet bakery, ArmorModel model) {
                throw new IllegalStateException("Attempted to create armor model wrapper for unspecified class");
            }
        };
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> of(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, BiFunction<ModelPart, ArmorModel, M> modelWrapper) {
        return of(rootId, modelCreator, modelWrapper, false);
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> of(ResourceLocation rootId, Function<ArmorModel, LayerDefinition> modelCreator, BiFunction<ModelPart, ArmorModel, M> modelWrapper, boolean slimTextures) {
        return new ArmorModelSet<>(rootId, modelCreator, modelWrapper, slimTextures);
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelSet<T, M> castOf(ArmorModelSet<?, ?> other, BiFunction<ModelPart, ArmorModel, M> modelWrapper) {
        return new ArmorModelSet<>(other.modelDefinitions, modelWrapper, other.slimTextures) {
            @Override
            public void registerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {}
        };
    }

    public void registerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        modelDefinitions.forEach((model, id) -> consumer.accept(id, () -> modelCreator.apply(model)));
    }

    public M createModel(EntityModelSet bakery, ArmorModel model) {
        return modelWrapper.apply(bakery.bakeLayer(modelDefinitions.get(model)), model);
    }

    public Map<ArmorModel, M> createModels(EntityModelSet bakery) {
        return modelDefinitions.keySet().stream().collect(Collectors.toUnmodifiableMap(Function.identity(), armorModel -> this.createModel(bakery, armorModel)));
    }

    public ModelLayerLocation getModelName(ArmorModel model) {
        return modelDefinitions.get(model);
    }
}
