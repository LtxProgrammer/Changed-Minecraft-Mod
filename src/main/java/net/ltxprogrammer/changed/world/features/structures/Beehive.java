package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.JunglePyramidFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class Beehive extends StructureFeature<NoneFeatureConfiguration> {
    private final GenerationStep.Decoration step;
    private final ResourceLocation nbt;

    public Beehive(Codec<NoneFeatureConfiguration> codec, GenerationStep.Decoration step, ResourceLocation nbt) {
        super(codec, PieceGeneratorSupplier.simple(Beehive::checkLocation, Beehive.generatePieces(nbt)));
        this.step = step;
        this.nbt = nbt;
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
            builder.addPiece(new BasicNBTPiece(nbt, null, context));
        };
    }

    @Override
    public GenerationStep.Decoration step() {
        return step;
    }
}
