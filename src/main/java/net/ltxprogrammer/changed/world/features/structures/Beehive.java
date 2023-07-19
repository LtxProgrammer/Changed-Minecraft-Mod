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

public class Beehive extends StructureFeature<NoneFeatureConfiguration> {
    private final GenerationStep.Decoration step;

    public Beehive(Codec<NoneFeatureConfiguration> codec, GenerationStep.Decoration step, ResourceLocation nbt) {
        super(codec, PieceGeneratorSupplier.simple(Beehive::checkLocation, Beehive.generatePieces(nbt)));
        this.step = step;
    }

    private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> context) {
        if (!context.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
            return false;
        } else {
            return context.getLowestY(12, 15) >= context.chunkGenerator().getSeaLevel();
        }
    }

    private static PieceGenerator<NoneFeatureConfiguration> generatePieces(ResourceLocation nbt) {
        return (builder, context) -> {
            builder.addPiece(new SurfaceNBTPiece(nbt, null, context));
        };
    }

    @Override
    public GenerationStep.Decoration step() {
        return step;
    }
}
