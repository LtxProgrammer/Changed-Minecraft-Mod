package net.ltxprogrammer.changed.extension.terrablender;

import net.ltxprogrammer.changed.init.ChangedBiomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.List;

public class LatexSurfaceRules {
    private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource GRASS_BLOCK = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());

    protected static SurfaceRules.RuleSource makeRules() {
        List<SurfaceRules.RuleSource> list = new ArrayList<>();
        ChangedBiomes.DESCRIPTORS.forEach((key, biomeDesc) -> {
            list.add(SurfaceRules.ifTrue(SurfaceRules.isBiome(key.getKey()), SurfaceRules.state(biomeDesc.groundBlock())));
        });

        list.add(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), GRASS_BLOCK), DIRT)));

        return new SurfaceRules.SequenceRuleSource(list);
    }
}
