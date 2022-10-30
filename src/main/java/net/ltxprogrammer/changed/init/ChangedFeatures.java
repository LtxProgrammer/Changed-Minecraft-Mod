package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.DeferredStateProvider;
import net.ltxprogrammer.changed.world.features.lakes.DarkLatexLake;
import net.ltxprogrammer.changed.world.features.lakes.WhiteLatexLake;
import net.ltxprogrammer.changed.world.features.structures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static net.minecraft.data.worldgen.placement.VegetationPlacements.treePlacement;

@Mod.EventBusSubscriber
public class ChangedFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, Changed.MODID);
    private static final List<FeatureRegistration> FEATURE_REGISTRATIONS = new ArrayList<>();
    private static final List<TreeRegistration> TREE_REGISTRATIONS = new ArrayList<>();
    public static final RegistryObject<Feature<?>> DARK_LATEX_LAKE = register("dark_latex_lake", DarkLatexLake::feature,
            new FeatureRegistration(GenerationStep.Decoration.LAKES, DarkLatexLake.GENERATE_BIOMES, DarkLatexLake::placedFeature));
    public static final RegistryObject<Feature<?>> WHITE_LATEX_LAKE = register("white_latex_lake", WhiteLatexLake::feature,
            new FeatureRegistration(GenerationStep.Decoration.LAKES, WhiteLatexLake.GENERATE_BIOMES, WhiteLatexLake::placedFeature));


    public static final Set<ResourceLocation> LAB_GENERATE_BIOMES = Set.of(
            new ResourceLocation("plains"),
            new ResourceLocation("meadow"),
            new ResourceLocation("snowy_plains"));

    public static final int LAB_RARITY = 500; // Lower is rarer
    public static final RegistryObject<Feature<?>> DECAYED_LAB_DARK_LATEX = register("decayed_lab_dark_latex", DecayedLabDarkLatex::feature,
            new FeatureRegistration(GenerationStep.Decoration.SURFACE_STRUCTURES, LAB_GENERATE_BIOMES, DecayedLabDarkLatex::placedFeature));
    public static final RegistryObject<Feature<?>> DECAYED_LAB_DARK_LATEX_DRAGON = register("decayed_lab_dark_latex_dragon", DecayedLabDarkLatexDragon::feature,
            new FeatureRegistration(GenerationStep.Decoration.SURFACE_STRUCTURES, LAB_GENERATE_BIOMES, DecayedLabDarkLatexDragon::placedFeature));
    public static final RegistryObject<Feature<?>> DECAYED_LAB_AQUA = register("decayed_lab_aqua", DecayedLabAqua::feature,
            new FeatureRegistration(GenerationStep.Decoration.SURFACE_STRUCTURES, LAB_GENERATE_BIOMES, DecayedLabAqua::placedFeature));
    public static final RegistryObject<Feature<?>> DECAYED_LAB_WHITE_LATEX = register("decayed_lab_wl", DecayedLabWhiteLatex::feature,
            new FeatureRegistration(GenerationStep.Decoration.SURFACE_STRUCTURES, LAB_GENERATE_BIOMES, DecayedLabWhiteLatex::placedFeature));
    public static final RegistryObject<Feature<?>> DECAYED_LAB_WHITE_ORIGIN = register("decayed_lab_origin", DecayedLabOrigin::feature,
            new FeatureRegistration(GenerationStep.Decoration.SURFACE_STRUCTURES, LAB_GENERATE_BIOMES, DecayedLabOrigin::placedFeature));
    public static final RegistryObject<Feature<?>> DECAYED_LAB_WHITE_TREATMENT = register("decayed_lab_treatment", DecayedLabTreatment::feature,
            new FeatureRegistration(GenerationStep.Decoration.SURFACE_STRUCTURES, LAB_GENERATE_BIOMES, DecayedLabTreatment::placedFeature));

    private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(BlockStateProvider p_195147_, BlockStateProvider p_195148_, int p_195149_, int p_195150_, int p_195151_, int p_195152_) {
        return new TreeConfiguration.TreeConfigurationBuilder(p_195147_, new StraightTrunkPlacer(p_195149_, p_195150_, p_195151_), p_195148_, new BlobFoliagePlacer(ConstantInt.of(p_195152_), ConstantInt.of(0), 3), new TwoLayersFeatureSize(1, 0, 1));
    }

    private static TreeConfiguration.TreeConfigurationBuilder createOrangeTree() {
        return createStraightBlobTree(BlockStateProvider.simple(Blocks.OAK_LOG), DeferredStateProvider.of(ChangedBlocks.ORANGE_TREE_LEAVES), 4, 2, 0, 2).ignoreVines();
    }

    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> ORANGE_TREE = FeatureUtils.register(Changed.modResourceStr("orange_tree"), Feature.TREE, createOrangeTree().build());
    public static final Holder<PlacedFeature> ORANGE_TREE_CHECKED = registerTree("orange_tree_checked", ORANGE_TREE, Set.of()/*new ResourceLocation("trees_birch_and_oak"), 0.1f*/, ChangedBlocks.ORANGE_TREE_SAPLING.getId());

    public static class DeferredWouldSurvivePredicate implements BlockPredicate {
        public static final Codec<DeferredWouldSurvivePredicate> CODEC = RecordCodecBuilder.create((p_190577_) -> {
            return p_190577_.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((p_190581_) -> {
                return p_190581_.offset;
            }), ResourceLocation.CODEC.fieldOf("block").forGetter((p_190579_) -> {
                return p_190579_.block;
            })).apply(p_190577_, DeferredWouldSurvivePredicate::new);
        });
        private final Vec3i offset;
        private final ResourceLocation block;

        public DeferredWouldSurvivePredicate(Vec3i offset, ResourceLocation block) {
            this.offset = offset;
            this.block = block;
        }

        @Override
        public BlockPredicateType<?> type() {
            return ChangedBlockPredicateTypes.DEFERRED_WOULD_SURVIVE;
        }

        @Override
        public boolean test(WorldGenLevel worldGenLevel, BlockPos blockPos) {
            return Registry.BLOCK.get(block).defaultBlockState().canSurvive(worldGenLevel, blockPos.offset(offset));
        }
    }

    private static Holder<PlacedFeature> registerTree(String name, Holder<ConfiguredFeature<TreeConfiguration, ?>> holder, Set<ResourceLocation> biomes, ResourceLocation sapling) {
        var reg = PlacementUtils.register(Changed.modResourceStr(name), holder, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1))/*BlockPredicateFilter.forPredicate(
                new DeferredWouldSurvivePredicate(BlockPos.ZERO, sapling))*/);
        TREE_REGISTRATIONS.add(new TreeRegistration(Either.left(biomes), 0.0f, () -> reg));
        return reg;
    }

    private static Holder<PlacedFeature> registerTree(String name, Holder<ConfiguredFeature<TreeConfiguration, ?>> holder, ResourceLocation hijackFeature, float weight, ResourceLocation sapling) {
        var reg = PlacementUtils.register(Changed.modResourceStr(name), holder, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1))/*BlockPredicateFilter.forPredicate(
                new DeferredWouldSurvivePredicate(BlockPos.ZERO, sapling))*/);
        TREE_REGISTRATIONS.add(new TreeRegistration(Either.right(hijackFeature), weight, () -> reg));
        return reg;
    }

    private static RegistryObject<Feature<?>> register(String registryName, Supplier<Feature<?>> feature, FeatureRegistration featureRegistration) {
        FEATURE_REGISTRATIONS.add(featureRegistration);
        return REGISTRY.register(registryName, feature);
    }

    @SubscribeEvent
    public static void addFeaturesToBiomes(BiomeLoadingEvent event) {
        for (FeatureRegistration registration : FEATURE_REGISTRATIONS) {
            if (registration.biomes() == null || registration.biomes().contains(event.getName()))
                event.getGeneration().getFeatures(registration.stage()).add(registration.placedFeature().get());
        }

        for (TreeRegistration registration : TREE_REGISTRATIONS) {
            registration.location.ifLeft(set -> {
                if (set.contains(event.getName()))
                    event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION).add(registration.placedFeature().get());
            }).ifRight(hijack -> {
                event.getGeneration().getFeatures(GenerationStep.Decoration.VEGETAL_DECORATION).forEach(placed -> {
                    if (placed.value().feature().is(hijack) && placed.value().feature().value().config() instanceof RandomFeatureConfiguration randomFeatureConfiguration) {
                        AtomicBoolean atomic = new AtomicBoolean(false);
                        randomFeatureConfiguration.features.forEach(weightedPlacedFeature ->
                                atomic.compareAndExchange(false, weightedPlacedFeature.feature == registration.placedFeature().get()));
                        if (!atomic.getAcquire()) {
                            ImmutableList.Builder<WeightedPlacedFeature> listBuilder = ImmutableList.builder();
                            listBuilder.addAll(randomFeatureConfiguration.features);
                            // TODO bruh `Tried to biome check an unregistered feature`
                            listBuilder.add(new WeightedPlacedFeature(registration.placedFeature().get(), registration.weight()));
                            randomFeatureConfiguration.features = listBuilder.build();
                        }
                    }
                });
            });
        }

        var spawner = event.getSpawns().getSpawner(MobCategory.MONSTER);
        ChangedEntities.SPAWNING_ENTITY.forEach(listConsumer -> listConsumer.accept(event, spawner));
    }

    private record FeatureRegistration(GenerationStep.Decoration stage, Set<ResourceLocation> biomes,
                                       Supplier<Holder<PlacedFeature>> placedFeature) {
    }

    private record TreeRegistration(Either<Set<ResourceLocation>, ResourceLocation> location, float weight,
                                    Supplier<Holder<PlacedFeature>> placedFeature) {
    }
}
