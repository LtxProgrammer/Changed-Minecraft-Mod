package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.function.Predicate;

public class DecayedLab extends StructureFeature<NoneFeatureConfiguration> {
    private final GenerationStep.Decoration step;

    public DecayedLab(Codec<NoneFeatureConfiguration> codec, GenerationStep.Decoration step, ResourceLocation nbt, ResourceLocation lootTable) {
        super(codec, PieceGeneratorSupplier.simple(DecayedLab.checkLocation(nbt), DecayedLab.generatePieces(nbt, lootTable)));
        this.step = step;
    }

    private static <C extends FeatureConfiguration> Predicate<PieceGeneratorSupplier.Context<C>> checkLocation(ResourceLocation nbt) {
        return context -> {
            if (!context.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
                return false;
            } else {
                StructureTemplate template = context.structureManager().get(nbt).orElseThrow();

                if (context.getLowestY(template.getSize().getX(), template.getSize().getZ()) < context.chunkGenerator().getSeaLevel())
                    return false;
                return true;
            }
        };
    }

    private static PieceGenerator<NoneFeatureConfiguration> generatePieces(ResourceLocation nbt, ResourceLocation lootTable) {
        return (builder, context) -> {
            builder.addPiece(new SurfaceNBTPiece(nbt, lootTable, context));
        };
    }

    @Override
    public GenerationStep.Decoration step() {
        return step;
    }
}
