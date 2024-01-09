package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class Facility extends StructureFeature<NoneFeatureConfiguration> {
    private final GenerationStep.Decoration step;

    public Facility(Codec<NoneFeatureConfiguration> codec, GenerationStep.Decoration step) {
        super(codec, PieceGeneratorSupplier.simple(Facility::checkLocation, Facility::generatePieces));
        this.step = step;
    }

    private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> context) {
        if (!context.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
            return false;
        } else {
            int[] heights = context.getCornerHeights(
                    context.chunkPos().getMinBlockX(), context.chunkPos().getMaxBlockX(),
                    context.chunkPos().getMinBlockZ(), context.chunkPos().getMaxBlockZ());
            int variation = Math.abs(Math.abs(heights[0] - heights[1]) - Math.abs(heights[2] - heights[3]));
            if (variation > 1)
                return false;

            int lowestY = context.getLowestY(12, 15);

            return lowestY >= context.chunkGenerator().getSeaLevel() && lowestY <= context.chunkGenerator().getSeaLevel() + 30;
        }
    }

    private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context) {
        FacilityPieces.generateFacility(builder, context, 5, 30);
    }

    @Override
    public GenerationStep.Decoration step() {
        return step;
    }
}
