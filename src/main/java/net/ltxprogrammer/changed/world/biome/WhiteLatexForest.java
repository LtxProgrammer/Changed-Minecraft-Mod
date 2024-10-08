package net.ltxprogrammer.changed.world.biome;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.DeferredStateProvider;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedMobCategories;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
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

public class WhiteLatexForest implements ChangedBiomeInterface {
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_PILLAR =
            FeatureUtils.register(Changed.modResourceStr("patch_white_latex_pillar"), Feature.RANDOM_PATCH,
                    grassPatch(DeferredStateProvider.of(ChangedBlocks.WHITE_LATEX_PILLAR), 8));


    public Biome build() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.caveSpawns(spawnBuilder);
        ChangedBiomeInterface.addSpawn(spawnBuilder, ChangedMobCategories.CHANGED, ChangedEntities.PURE_WHITE_LATEX_WOLF, 50, 1, 3, 0.7, 0.15);
        ChangedBiomeInterface.addSpawn(spawnBuilder, ChangedMobCategories.CHANGED, ChangedEntities.LATEX_MUTANT_BLOODCELL_WOLF, 1, 1, 3, 0.7, 0.15);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        BiomeDefaultFeatures.addDefaultOres(genBuilder);

        genBuilder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register(Changed.modResourceStr("white_latex_forest_pillar"), PATCH_PILLAR,
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
        biomeBuilder.biomeCategory(Biome.BiomeCategory.NONE);
        biomeBuilder.downfall(0.8f);
        return biomeBuilder.build();
    }

    @Override
    public BlockState groundBlock() {
        return Blocks.DIRT.defaultBlockState().setValue(COVERED, LatexType.WHITE_LATEX);
    }

    @Override
    public BlockState undergroundBlock() {
        return Blocks.DIRT.defaultBlockState().setValue(COVERED, LatexType.WHITE_LATEX);
    }

    @Override
    public BlockState underwaterBlock() {
        return Blocks.DIRT.defaultBlockState().setValue(COVERED, LatexType.WHITE_LATEX);
    }
    @Override
    public BlockState waterBlock() {
        return ChangedBlocks.WHITE_LATEX_FLUID.get().defaultBlockState();
    }

    @Override
    public List<Climate.ParameterPoint> getPoints() {
        return new ParameterPointsBuilder()
                .temperature(Temperature.COOL)
                .humidity(Humidity.FULL_RANGE)
                .continentalness(Continentalness.span(Continentalness.MID_INLAND, Continentalness.FAR_INLAND))
                .erosion(Erosion.EROSION_0, Erosion.EROSION_1)
                .depth(Depth.SURFACE, Depth.FLOOR)
                .weirdness(Weirdness.span(Weirdness.VALLEY, Weirdness.MID_SLICE_VARIANT_ASCENDING))
                .build();
    }
}
