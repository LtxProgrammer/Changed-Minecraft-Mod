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

public class WhiteLatexForest implements ChangedBiomeInterface {
    public static final Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> PATCH_PILLAR =
            FeatureUtils.register(Changed.modResourceStr("patch_white_latex_pillar"), Feature.RANDOM_PATCH,
                    grassPatch(DeferredStateProvider.of(ChangedBlocks.WHITE_LATEX_PILLAR), 8));

    public static final Climate.ParameterPoint PARAMETER_POINT = new Climate.ParameterPoint(
            Climate.Parameter.span(-0.15f, 0.1f),
            Climate.Parameter.span(-0.125f, 0.125f),
            farInlandContinentalness,
            Climate.Parameter.span(-0.75f, 0.0f),
            Climate.Parameter.point(0),
            Climate.Parameter.span(-0.25f, 0.25f), 0);


    public Biome build() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        ChangedEntities.Category.WHITE_LATEX.forEach(
                (entityType) -> spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(entityType.get(), 1, 1, 1)));

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
    public Climate.ParameterPoint climate() {
        return PARAMETER_POINT;
    }
}
