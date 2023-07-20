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

    private static <C extends FeatureConfiguration > boolean checkLocation(PieceGeneratorSupplier.Context<C> context) {
        if (!context.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
            return false;
        } else {
            return context.getLowestY(12, 15) >= context.chunkGenerator().getSeaLevel();
        }
    }

    private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context) {
        FacilityPieces.generateFacility(builder, context, 0, 15);
    }

    @Override
    public GenerationStep.Decoration step() {
        return step;
    }
}
