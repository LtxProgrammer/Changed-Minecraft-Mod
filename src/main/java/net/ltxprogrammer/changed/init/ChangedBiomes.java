package net.ltxprogrammer.changed.init;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.biome.ChangedBiomeInterface;
import net.ltxprogrammer.changed.world.biome.DarkLatexPlains;
import net.ltxprogrammer.changed.world.biome.WhiteLatexForest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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

    static record IsBlockCheck(Block block) implements SurfaceRules.ConditionSource {
        static final Codec<IsBlockCheck> CODEC = RecordCodecBuilder.create((p_189753_) -> {
            return p_189753_.group(Codec.STRING.fieldOf("block").forGetter((checker) -> checker.block.getRegistryName().toString())).apply(p_189753_, (string) -> new IsBlockCheck(Registry.BLOCK.get(new ResourceLocation(string))));
        });

        public Codec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(final SurfaceRules.Context p_189755_) {
            return () -> p_189755_.chunk.getSection(p_189755_.chunk.getSectionIndex(p_189755_.blockY))
                    .getBlockState(p_189755_.blockX & 15, p_189755_.blockY & 15, p_189755_.blockZ & 15).is(block);
        }
    }

    public static SurfaceRules.ConditionSource isBlockCheck(Block block) {
        return new IsBlockCheck(block);
    }
}
