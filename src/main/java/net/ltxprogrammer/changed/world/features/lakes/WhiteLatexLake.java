package net.ltxprogrammer.changed.world.features.lakes;

import com.google.common.collect.ImmutableSet;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.DeferredStateProvider;
import net.ltxprogrammer.changed.init.ChangedBiomes;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;
import java.util.Set;

public class WhiteLatexLake extends LakeFeature {
    public static WhiteLatexLake FEATURE = null;
    public static Holder<ConfiguredFeature<Configuration, ?>> CONFIGURED_FEATURE = null;
    public static Holder<PlacedFeature> PLACED_FEATURE = null;

    public static Feature<?> feature() {
        if (FEATURE == null) {
            FEATURE = new WhiteLatexLake();
            CONFIGURED_FEATURE = FeatureUtils.register(Changed.modResourceStr("white_latex_fluid"), FEATURE, new LakeFeature.Configuration(
                    DeferredStateProvider.of(ChangedBlocks.WHITE_LATEX_FLUID), BlockStateProvider.simple(Blocks.AIR)));
            PLACED_FEATURE = PlacementUtils.register(Changed.modResourceStr("white_latex_fluid"), CONFIGURED_FEATURE,
                    List.of(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT,
                            EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), 32),
                            BiomeFilter.biome()));
        }
        return FEATURE;
    }

    public static Holder<PlacedFeature> placedFeature() {
        return PLACED_FEATURE;
    }

    public static final ImmutableSet<ResourceLocation> GENERATE_BIOMES = ImmutableSet.of(ChangedBiomes.WHITE_LATEX_FOREST.getId());

    private final Set<ResourceKey<Level>> generate_dimensions = Set.of(Level.OVERWORLD);

    public WhiteLatexLake() {
        super(LakeFeature.Configuration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        WorldGenLevel world = context.level();
        if (!generate_dimensions.contains(world.getLevel().dimension()))
            return false;

        return super.place(context);
    }

}
