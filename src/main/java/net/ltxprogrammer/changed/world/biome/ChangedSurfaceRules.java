package net.ltxprogrammer.changed.world.biome;

import net.ltxprogrammer.changed.init.ChangedBiomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ChangedSurfaceRules {
    private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource GRASS_BLOCK = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());

    public static List<SurfaceRules.RuleSource> makeRules() {
        List<SurfaceRules.RuleSource> rules = new ArrayList<>();
        ChangedBiomes.DESCRIPTORS.forEach((key, biomeDesc) -> {
            rules.add(SurfaceRules.ifTrue(
                    SurfaceRules.isBiome(key.getKey()),
                    SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
                            SurfaceRules.state(biomeDesc.groundBlock()))));
        });
        /*rules.add(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), GRASS_BLOCK), DIRT)));*/


        return rules;
    }
}
