package net.ltxprogrammer.changed.ability.tree;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class AbilityCounter {
    public final TransfurVariantInstance<?> variantInstance;
    public final IAbstractChangedEntity entity;
    private final Set<ResourceLocation> enabledFeatures = new HashSet<>();
    private final Map<Attribute, Double> baselineAttributes;
    private final Map<Attribute, Double> attributeAdders;
    private final Map<AbstractAbility<?>, AbstractAbilityInstance> activeAbilities;
    private final Map<NodeEffect, AbilityTree.NodeEffectInstance> nodeEffectInstances;

    private static Map<Attribute, Double> getBaseAttributeValues(AttributeMap attributeMap) {
        Map<Attribute, Double> map = new HashMap<>();
        ForgeRegistries.ATTRIBUTES.getValues().stream()
                .filter(attributeMap::hasAttribute)
                .forEach(attribute -> map.put(attribute, attributeMap.getBaseValue(attribute)));
        return map;
    }

    public AbilityCounter(TransfurVariantInstance<?> variantInstance) {
        this.variantInstance = variantInstance;
        this.entity = IAbstractChangedEntity.forPlayer(variantInstance.getHost());
        this.attributeAdders = getBaseAttributeValues(variantInstance.getHost().getAttributes());
        this.baselineAttributes = Map.copyOf(attributeAdders);
        this.activeAbilities = new HashMap<>(variantInstance.abilityInstances);
        this.nodeEffectInstances = new HashMap<>();

        this.attributeAdders.replaceAll((a, v) -> 0.0);
    }

    public Map<Attribute, Double> getAttributeAdders() {
        return attributeAdders;
    }

    /**
     * Adds a named feature that built-in processes can check for
     * @param resourceLocation name of the feature
     */
    public void addEnabledFeature(ResourceLocation resourceLocation) {
        enabledFeatures.add(resourceLocation);
    }

    public void addAttributeMultiplier(Attribute attribute, double basePercent) {
        if (!baselineAttributes.containsKey(attribute))
            return;

        attributeAdders.computeIfPresent(attribute, (attr, current) -> {
            return current + baselineAttributes.get(attr) * basePercent;
        });
    }

    public void addAttributeAdder(Attribute attribute, double value) {
        if (!baselineAttributes.containsKey(attribute))
            return;

        attributeAdders.computeIfPresent(attribute, (attr, current) -> {
            return current + value;
        });
    }

    public AbilityTree.NodeEffectInstance addEffectInstance(NodeEffect key, Function<IAbstractChangedEntity, AbilityTree.NodeEffectInstance> ctor) {
        return nodeEffectInstances.computeIfAbsent(key, ignored -> ctor.apply(this.entity));
    }
}
