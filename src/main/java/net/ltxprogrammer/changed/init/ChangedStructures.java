package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.features.structures.Beehive;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public class ChangedStructures {
    public static final DeferredRegister<StructureFeature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, Changed.MODID);
    public static final DeferredRegister<ConfiguredStructureFeature<?, ?>> CONFIGURED_REGISTRY =
            DeferredRegister.create(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.key(), Changed.MODID);

    public static final Holder<ConfiguredStructureFeature<?, ?>> BEEHIVE1 = registerBeehive("beehive1");

    private static Holder<ConfiguredStructureFeature<?, ?>> registerBeehive(String nbt) {
        RegistryObject<StructureFeature<NoneFeatureConfiguration>> beehive = registerFeature(nbt, new Beehive(
                NoneFeatureConfiguration.CODEC,
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                Changed.modResource(nbt)));
        return registerConfigured(nbt, () -> beehive.get().configured(NoneFeatureConfiguration.INSTANCE, ChangedTags.Biomes.HAS_BEEHIVES));
    }

    private static <F extends StructureFeature<?>> RegistryObject<F> registerFeature(String name, F feature) {
        return REGISTRY.register(name, () -> feature);
    }

    private static <FC extends FeatureConfiguration, F extends StructureFeature<FC>>
    Holder<ConfiguredStructureFeature<?, ?>> registerConfigured(String key, Supplier<ConfiguredStructureFeature<FC, F>> feature) {
        RegistryObject<ConfiguredStructureFeature<?, ?>> registered = CONFIGURED_REGISTRY.register(key, feature);
        return BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getOrCreateHolder(Objects.requireNonNull(registered.getKey()));
    }
}
