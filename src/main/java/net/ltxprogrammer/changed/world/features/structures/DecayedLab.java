package net.ltxprogrammer.changed.world.features.structures;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class DecayedLab extends BasicNBTStructure {
    private static Map<ResourceLocation, DecayedLab> FEATURES = new HashMap<>();
    private static Map<ResourceLocation, Holder<PlacedFeature>> PLACED_FEATURES = new HashMap<>();

    public static final int LAB_RARITY = 300; // Lower is rarer
    private final int yOffset;
    private final ResourceLocation lootTable;
    private final boolean underground;
    public DecayedLab(ResourceLocation structureNBT, int yOffset, ResourceLocation lootTable, boolean underground) {
        super(structureNBT);
        this.yOffset = yOffset;
        this.lootTable = lootTable;
        this.underground = underground;
    }

    public static Supplier<Feature<?>> feature(ResourceLocation structureNBT, int yOffset, ResourceLocation lootTable, boolean underground) {
        return () -> FEATURES.computeIfAbsent(structureNBT, location ->
                new DecayedLab(structureNBT, yOffset, lootTable, underground));
    }

    public static Supplier<Holder<PlacedFeature>> placedFeature(ResourceLocation id) {
        return () -> PLACED_FEATURES.computeIfAbsent(id, location -> PlacementUtils.register(location.toString(),
                FeatureUtils.register(location.toString(), FEATURES.get(id), FeatureConfiguration.NONE), List.of()));
    }

    @Override
    public boolean allowedIn(ResourceKey<Level> dimension) {
        return dimension.equals(Level.OVERWORLD);
    }

    @Override
    public boolean testChance(Random random) {
        return random.nextInt(1000000) + 1 <= LAB_RARITY;
    }

    @Override
    public ResourceLocation getLootTable() {
        return lootTable;
    }

    @Override
    public boolean underground() {
        return underground;
    }

    @Override
    public int getYOffset() {
        return yOffset;
    }
}
