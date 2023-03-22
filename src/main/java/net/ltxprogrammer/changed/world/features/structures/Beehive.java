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

public class Beehive extends BasicNBTStructure {
    private static final Map<ResourceLocation, Beehive> FEATURES = new HashMap<>();
    private static final Map<ResourceLocation, Holder<PlacedFeature>> PLACED_FEATURES = new HashMap<>();

    public static final int LAB_RARITY = 300; // Lower is rarer
    private final int yOffset;
    private final boolean underground;
    public Beehive(ResourceLocation structureNBT, int yOffset, boolean underground) {
        super(structureNBT);
        this.yOffset = yOffset;
        this.underground = underground;
    }

    public static Supplier<Feature<?>> feature(ResourceLocation structureNBT, int yOffset, boolean underground) {
        return () -> FEATURES.computeIfAbsent(structureNBT, location ->
                new Beehive(structureNBT, yOffset, underground));
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
        return null;
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
