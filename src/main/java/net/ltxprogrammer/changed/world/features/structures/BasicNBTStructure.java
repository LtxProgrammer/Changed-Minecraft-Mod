package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;
import java.util.Random;

public abstract class BasicNBTStructure extends Feature<NoneFeatureConfiguration> {
    private final ResourceLocation nbt;

    public BasicNBTStructure(ResourceLocation structureNBT) {
        super(NoneFeatureConfiguration.CODEC);
        this.nbt = structureNBT;
    }

    public abstract boolean allowedIn(ResourceKey<Level> dimension);
    public abstract boolean testChance(Random random);
    public abstract ResourceLocation getLootTable();
    public abstract boolean underground();
    public int getYOffset() {
        return -1;
    }

    private Optional<StructureTemplate> template = null;
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!allowedIn(context.level().getLevel().dimension()))
            return false;

        if (template == null)
            template = context.level().getLevel().getStructureManager().get(nbt);
        if (template.isEmpty())
            return false;

        boolean anyPlaced = false;
        if (testChance(context.random())) {
            int count = context.random().nextInt(1) + 1;
            for (int a = 0; a < count; a++) {
                int i = context.origin().getX() + context.random().nextInt(16);
                int k = context.origin().getZ() + context.random().nextInt(16);

                int j = underground() ?
                        context.random().nextInt(-40, context.level().getSeaLevel() - 15) :
                        context.level().getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, k) - 1;

                BlockPos spawnTo = new BlockPos(i, j + getYOffset(), k);

                if (spawnTo.getY() < context.level().getSeaLevel())
                    continue;

                if (template.get().placeInWorld(context.level(), spawnTo, spawnTo,
                        new StructurePlaceSettings().setMirror(Mirror.values()[context.random().nextInt(2)])
                                .setRotation(Rotation.values()[context.random().nextInt(3)]).setRandom(context.random())
                                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                                .addProcessor(ChestLootTableProcessor.of(getLootTable()))
                                .setIgnoreEntities(false),
                        context.random(), 2)) {

                    anyPlaced = true;
                }
            }
        }

        return anyPlaced;
    }
}
