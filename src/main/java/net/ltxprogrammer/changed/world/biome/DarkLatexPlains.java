package net.ltxprogrammer.changed.world.biome;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.DeferredStateProvider;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;

import java.util.List;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;
import static net.ltxprogrammer.changed.init.ChangedBiomes.calculateSkyColor;
import static net.ltxprogrammer.changed.init.ChangedBiomes.grassPatch;

public class DarkLatexPlains implements ChangedBiomeInterface {
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_CRYSTAL =
            FeatureUtils.register(Changed.modResourceStr("patch_crystal"), Feature.RANDOM_PATCH,
                grassPatch(DeferredStateProvider.of(ChangedBlocks.LATEX_CRYSTAL), 16));
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_BEIFENG_CRYSTAL =
            FeatureUtils.register(Changed.modResourceStr("patch_beifeng_crystal"), Feature.RANDOM_PATCH,
                grassPatch(DeferredStateProvider.of(ChangedBlocks.LATEX_BEIFENG_CRYSTAL), 4));
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_BEIFENG_CRYSTAL_SMALL =
            FeatureUtils.register(Changed.modResourceStr("patch_beifeng_crystal_small"), Feature.RANDOM_PATCH,
                    grassPatch(DeferredStateProvider.of(ChangedBlocks.LATEX_BEIFENG_CRYSTAL_SMALL), 4));
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_DARK_LATEX_CRYSTAL_LARGE =
            FeatureUtils.register(Changed.modResourceStr("patch_dark_latex_crystal_large"), Feature.RANDOM_PATCH,
                    grassPatch(DeferredStateProvider.of(ChangedBlocks.DARK_LATEX_CRYSTAL_LARGE), 6));
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_DARK_LATEX_DRAGON_CRYSTAL =
            FeatureUtils.register(Changed.modResourceStr("patch_dark_latex_dragon_crystal"), Feature.RANDOM_PATCH,
                    grassPatch(DeferredStateProvider.of(ChangedBlocks.DARK_LATEX_DRAGON_CRYSTAL), 2));
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_WOLF_CRYSTAL =
            FeatureUtils.register(Changed.modResourceStr("patch_wolf_crystal"), Feature.RANDOM_PATCH,
                grassPatch(DeferredStateProvider.of(ChangedBlocks.LATEX_WOLF_CRYSTAL), 4));
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_WOLF_CRYSTAL_SMALL =
            FeatureUtils.register(Changed.modResourceStr("patch_wolf_crystal_small"), Feature.RANDOM_PATCH,
                    grassPatch(DeferredStateProvider.of(ChangedBlocks.LATEX_WOLF_CRYSTAL_SMALL), 4));

    public Biome build() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        ChangedEntities.Category.DARK_LATEX.forEach((entityType) -> {
            spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(entityType.get(), 1, 1, 1));
        });

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        BiomeDefaultFeatures.addDefaultOres(genBuilder);

        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("dark_latex_plains_crystal"), PATCH_CRYSTAL,
                        List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                BiomeFilter.biome())));
        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("dark_latex_plains_beifeng_crystal"), PATCH_BEIFENG_CRYSTAL,
                        List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                BiomeFilter.biome())));
        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("dark_latex_plains_beifeng_crystal_small"), PATCH_BEIFENG_CRYSTAL_SMALL,
                        List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                BiomeFilter.biome())));
        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("dark_latex_plains_dark_latex_crytal_large"), PATCH_DARK_LATEX_CRYSTAL_LARGE,
                        List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                BiomeFilter.biome())));
        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("dark_latex_plains_dark_latex_dragon_crystal"), PATCH_DARK_LATEX_DRAGON_CRYSTAL,
                        List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                BiomeFilter.biome())));
        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("dark_latex_plains_wolf_crystal"), PATCH_WOLF_CRYSTAL,
                        List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                BiomeFilter.biome())));
        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("dark_latex_plains_wolf_crystal_small"), PATCH_WOLF_CRYSTAL_SMALL,
                        List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                                BiomeFilter.biome())));

        BiomeSpecialEffects.Builder sfxBuilder = new BiomeSpecialEffects.Builder();
        sfxBuilder.fogColor(12638463);
        sfxBuilder.waterColor(4159204);
        sfxBuilder.waterFogColor(329011);
        sfxBuilder.skyColor(7972607);
        sfxBuilder.foliageColorOverride(10387789);
        sfxBuilder.grassColorOverride(9470285);

        sfxBuilder.skyColor(calculateSkyColor(0.5f));

        Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder();
        biomeBuilder.mobSpawnSettings(spawnBuilder.build());
        biomeBuilder.generationSettings(genBuilder.build());
        biomeBuilder.specialEffects(sfxBuilder.build());
        biomeBuilder.precipitation(Biome.Precipitation.NONE);
        biomeBuilder.temperature(0.5f);
        biomeBuilder.biomeCategory(Biome.BiomeCategory.PLAINS);
        biomeBuilder.downfall(0.8f);
        return biomeBuilder.build();
    }

    @Override
    public BlockState groundBlock() {
        return Blocks.DIRT.defaultBlockState().setValue(COVERED, LatexType.DARK_LATEX);
    }

    @Override
    public BlockState undergroundBlock() {
        return Blocks.DIRT.defaultBlockState().setValue(COVERED, LatexType.DARK_LATEX);
    }

    @Override
    public BlockState underwaterBlock() {
        return Blocks.DIRT.defaultBlockState().setValue(COVERED, LatexType.DARK_LATEX);
    }

    @Override
    public BlockState waterBlock() {
        return ChangedBlocks.DARK_LATEX_FLUID.get().defaultBlockState();
    }

    @Override
    public List<Climate.ParameterPoint> getPoints() {
        return new ParameterPointsBuilder()
                .temperature(Temperature.NEUTRAL, Temperature.WARM)
                .humidity(Humidity.FULL_RANGE)
                .continentalness(Continentalness.span(Continentalness.MID_INLAND, Continentalness.FAR_INLAND))
                .erosion(Erosion.EROSION_0, Erosion.EROSION_1)
                .depth(Depth.SURFACE, Depth.FLOOR)
                .weirdness(Weirdness.span(Weirdness.VALLEY, Weirdness.MID_SLICE_VARIANT_ASCENDING))
                .build();
    }
}
