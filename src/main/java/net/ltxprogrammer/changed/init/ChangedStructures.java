package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.features.structures.Beehive;
import net.ltxprogrammer.changed.world.features.structures.DecayedLab;
import net.ltxprogrammer.changed.world.features.structures.Facility;
import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
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
    public static final Holder<ConfiguredStructureFeature<?, ?>> AQUATIC1 = registerLab("aquatic1", LootTables.DECAYED_LAB_AQUA);
    public static final Holder<ConfiguredStructureFeature<?, ?>> AQUATIC2 = registerLab("aquatic2", LootTables.DECAYED_LAB_AQUA);
    public static final Holder<ConfiguredStructureFeature<?, ?>> HUMAN_RESEARCH_LAB1 = registerLab("human_research_lab1",LootTables.DECAYED_LAB_TREATMENT);
    public static final Holder<ConfiguredStructureFeature<?, ?>> LASER_LAB1 = registerLab("laser_lab1", LootTables.LOW_TIER_LAB);
    public static final Holder<ConfiguredStructureFeature<?, ?>> OFFICE_AREA1 = registerLab("office_area1", LootTables.DECAYED_LAB_DL, ChangedTags.Biomes.HAS_DARK_DECAYED_LABS);
    public static final Holder<ConfiguredStructureFeature<?, ?>> OFFICE_AREA2 = registerLab("office_area2", LootTables.DECAYED_LAB_DL, ChangedTags.Biomes.HAS_DARK_DECAYED_LABS);
    public static final Holder<ConfiguredStructureFeature<?, ?>> RESEARCH_TOWER1 = registerLab("research_tower1", LootTables.DECAYED_LAB_ORIGIN);
    public static final Holder<ConfiguredStructureFeature<?, ?>> RESEARCH_TOWER2 = registerLab("research_tower2", LootTables.DECAYED_LAB_ORIGIN);
    public static final Holder<ConfiguredStructureFeature<?, ?>> RESEARCH_TOWER3 = registerLab("research_tower3", LootTables.DECAYED_LAB_ORIGIN);
    public static final Holder<ConfiguredStructureFeature<?, ?>> RESEARCH_TOWER4 = registerLab("research_tower4", LootTables.DECAYED_LAB_ORIGIN);
    public static final Holder<ConfiguredStructureFeature<?, ?>> TREATMENT_1 = registerLab("treatment_1", LootTables.HIGH_TIER_LAB);
    public static final Holder<ConfiguredStructureFeature<?, ?>> TREATMENT_2 = registerLab("treatment_2", LootTables.HIGH_TIER_LAB);
    public static final Holder<ConfiguredStructureFeature<?, ?>> TREATMENT_3 = registerLab("treatment_3", LootTables.HIGH_TIER_LAB);
    // TODO replace with procedural gen facility that stretches underground
    //public static final Holder<ConfiguredStructureFeature<?, ?>> UNDERGROUND_DL_LAB1 = registerLab("underground_dl_lab1", LootTables.DECAYED_LAB_DL);
    public static final Holder<ConfiguredStructureFeature<?, ?>> WHITE_LATEX_LAB1 = registerLab("white_latex_lab1", LootTables.DECAYED_LAB_WL, ChangedTags.Biomes.HAS_WHITE_DECAYED_LABS);
    public static final Holder<ConfiguredStructureFeature<?, ?>> WHITE_LATEX_LAB2 = registerLab("white_latex_lab2", LootTables.DECAYED_LAB_WL, ChangedTags.Biomes.HAS_WHITE_DECAYED_LABS);

    public static final Holder<ConfiguredStructureFeature<?, ?>> FACILITY = registerFacility("facility");

    private static Holder<ConfiguredStructureFeature<?, ?>> registerLab(String nbt, ResourceLocation lootTable) {
        return registerLab(nbt, lootTable, ChangedTags.Biomes.HAS_DECAYED_LABS);
    }

    private static Holder<ConfiguredStructureFeature<?, ?>> registerLab(String nbt, ResourceLocation lootTable, TagKey<Biome> key) {
        RegistryObject<StructureFeature<NoneFeatureConfiguration>> lab = registerFeature(nbt, new DecayedLab(
                NoneFeatureConfiguration.CODEC,
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                Changed.modResource(nbt), lootTable));
        return registerConfigured(nbt, () -> lab.get().configured(NoneFeatureConfiguration.INSTANCE, key));
    }

    private static Holder<ConfiguredStructureFeature<?, ?>> registerBeehive(String nbt) {
        RegistryObject<StructureFeature<NoneFeatureConfiguration>> beehive = registerFeature(nbt, new Beehive(
                NoneFeatureConfiguration.CODEC,
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                Changed.modResource(nbt)));
        return registerConfigured(nbt, () -> beehive.get().configured(NoneFeatureConfiguration.INSTANCE, ChangedTags.Biomes.HAS_BEEHIVES));
    }

    private static Holder<ConfiguredStructureFeature<?, ?>> registerFacility(String name) {
        RegistryObject<StructureFeature<NoneFeatureConfiguration>> facility = registerFeature(name, new Facility(
                NoneFeatureConfiguration.CODEC,
                GenerationStep.Decoration.UNDERGROUND_STRUCTURES));
        return registerConfigured(name, () -> facility.get().configured(NoneFeatureConfiguration.INSTANCE, ChangedTags.Biomes.HAS_FACILITY));
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
