package net.ltxprogrammer.changed.init;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.biome.ChangedBiomeInterface;
import net.ltxprogrammer.changed.world.biome.ChangedSurfaceRules;
import net.ltxprogrammer.changed.world.biome.DarkLatexPlains;
import net.ltxprogrammer.changed.world.biome.WhiteLatexForest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ChangedBiomes {
    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0f, 1.0f);

    public static final Map<RegistryObject<Biome>, ChangedBiomeInterface> DESCRIPTORS = new HashMap<>();
    public static final DeferredRegister<Biome> REGISTRY = DeferredRegister.create(ForgeRegistries.BIOMES, Changed.MODID);
    public static final RegistryObject<Biome> DARK_LATEX_PLAINS = register("dark_latex_plains", DarkLatexPlains::new);
    public static final RegistryObject<Biome> WHITE_LATEX_FOREST = register("white_latex_forest", WhiteLatexForest::new);

    public static <T extends ChangedBiomeInterface> RegistryObject<Biome> register(String name, Supplier<T> supplier) {
        ChangedBiomeInterface biomeDesc = supplier.get();
        RegistryObject<Biome> regObj = REGISTRY.register(name, biomeDesc::build);
        DESCRIPTORS.put(regObj, biomeDesc);
        return regObj;
    }

    public static int calculateSkyColor(float p_194844_) {
        float $$1 = p_194844_ / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }

    public static RandomPatchConfiguration grassPatch(BlockStateProvider p_195203_, int p_195204_) {
        return FeatureUtils.simpleRandomPatchConfiguration(p_195204_, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(p_195203_)));
    }

    @Mod.EventBusSubscriber
    public static class BiomeInjector {
        static final Logger LOGGER = LogManager.getLogger(BiomeInjector.class);

        @SubscribeEvent
        public static void onServerAboutToStart(ServerAboutToStartEvent event) {
            MinecraftServer server = event.getServer();
            Registry<DimensionType> dimensionRegistry = server.registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
            Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            WorldGenSettings worldGenSettings = server.getWorldData().worldGenSettings();
            for (var entry : worldGenSettings.dimensions().entrySet()) {
                DimensionType dimension = entry.getValue().typeHolder().value();
                if (dimension != dimensionRegistry.getOrThrow(DimensionType.OVERWORLD_LOCATION))
                    continue;
                ChunkGenerator chunkGenerator = entry.getValue().generator();
                // Inject biomes to biome source
                if (chunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource noiseSource) {
                    LOGGER.info("Injecting biomes into {}...", DimensionType.OVERWORLD_LOCATION);
                    List<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters = new ArrayList<>(noiseSource.parameters.values());
                    DESCRIPTORS.forEach((biome, desc) -> {
                        var points = desc.getPoints();
                        points.forEach(parameterPoint ->
                                parameters.add(new Pair<>(parameterPoint,
                                        biomeRegistry.getOrCreateHolder(
                                                ResourceKey.create(Registry.BIOME_REGISTRY, biome.getId())
                                        )
                                ))
                        );
                        LOGGER.info("Injected biome {} using {} parameter points", biome.getId(), points.size());
                    });

                    MultiNoiseBiomeSource moddedNoiseSource = new MultiNoiseBiomeSource(new Climate.ParameterList<>(parameters),
                            noiseSource.preset);
                    chunkGenerator.biomeSource = moddedNoiseSource;
                    chunkGenerator.runtimeBiomeSource = moddedNoiseSource;
                }
                // Inject surface rules
                if (chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
                    LOGGER.info("Injecting biome generation rules into {}...", DimensionType.OVERWORLD_LOCATION);
                    NoiseGeneratorSettings noiseGeneratorSettings = noiseGenerator.settings.value();
                    SurfaceRules.RuleSource currentRuleSource = noiseGeneratorSettings.surfaceRule();
                    if (currentRuleSource instanceof SurfaceRules.SequenceRuleSource sequenceRuleSource) {
                        List<SurfaceRules.RuleSource> surfaceRules = new ArrayList<>(ChangedSurfaceRules.makeRules());
                        surfaceRules.addAll(sequenceRuleSource.sequence());
                        NoiseGeneratorSettings moddedNoiseGeneratorSettings = new NoiseGeneratorSettings(noiseGeneratorSettings.noiseSettings(),
                                noiseGeneratorSettings.defaultBlock(), noiseGeneratorSettings.defaultFluid(),
                                noiseGeneratorSettings.noiseRouter(),
                                SurfaceRules.sequence(surfaceRules.toArray(SurfaceRules.RuleSource[]::new)),
                                noiseGeneratorSettings.seaLevel(), noiseGeneratorSettings.disableMobGeneration(),
                                noiseGeneratorSettings.aquifersEnabled(), noiseGeneratorSettings.oreVeinsEnabled(),
                                noiseGeneratorSettings.useLegacyRandomSource());
                        noiseGenerator.settings = new Holder.Direct<>(moddedNoiseGeneratorSettings);
                    }
                    LOGGER.info("Done injecting biome generation rules");
                }
            }
        }
    }
}
