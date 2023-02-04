package net.ltxprogrammer.changed.extension.terrablender;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.init.ChangedBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class LatexRegion extends Region {
    public LatexRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
            ChangedBiomes.DESCRIPTORS.forEach((key, biomeData) -> {
                biomeData.getPoints().forEach(point -> builder.replaceBiome(point, key.getKey()));
            });
        });
    }
}
