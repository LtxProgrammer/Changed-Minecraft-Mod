package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.features.structures.facility.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.*;

public class FacilityPieces {
    public static final FacilityPiece ENTRANCE_RED = new FacilityEntrance(Zone.RED_ZONE, Changed.modResource("facility/entrance_red"));
    public static final FacilityPiece ENTRANCE_BLUE = new FacilityEntrance(Zone.BLUE_ZONE, Changed.modResource("facility/entrance_blue"));
    public static final FacilityPieceCollection ENTRANCES = FacilityPieceCollection.of(ENTRANCE_RED, ENTRANCE_BLUE);
    public static final FacilityPiece CORRIDOR_RED = new FacilityStaircase(Zone.RED_ZONE, Changed.modResource("facility/corridor_red"));
    public static final FacilityPiece CORRIDOR_RED_2 = new FacilityStaircase(Zone.RED_ZONE, Changed.modResource("facility/corridor_red_2"));
    public static final FacilityPieceCollection STAIRCASES = FacilityPieceCollection.of(CORRIDOR_RED, CORRIDOR_RED_2);
    public static final FacilityPieceCollection CORRIDORS = FacilityPieceCollection.of();
    public static final FacilityPieceCollection ROOMS = FacilityPieceCollection.of();

    public static final Map<PieceType, FacilityPieceCollection> BY_PIECE_TYPE = Util.make(new HashMap<>(), map -> {
        map.put(PieceType.ENTRANCE, ENTRANCES);
        map.put(PieceType.STAIRCASE, STAIRCASES);
        map.put(PieceType.CORRIDOR, CORRIDORS);
        map.put(PieceType.ROOM, ROOMS);
    });

    private static void treeGenerate(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context,
                                     FacilityPiece parent, StructurePiece parentStructure,
                                     GenStep start, int genDepth, int span) {
        int reroll = 10;
        while (reroll > 0) {
            var type = start.validTypes().getRandom(context.random());
            if (type.isEmpty())
                return;
            var entry = type.get();

            var collection = BY_PIECE_TYPE.get(entry.getData());
            var nextPiece = collection.findNextPiece(context.random(), parent.zone).orElseGet(() ->
                    collection.findNextPiece(context.random()).orElse(null));
            if (nextPiece == null) {
                reroll--;
                continue;
            }
            var nextStructure = nextPiece.createStructurePiece(context.structureManager(), genDepth);
            if (!nextStructure.setupBoundingBox(builder, start.blockInfo())) {
                reroll--;
                continue;
            }

            builder.addPiece(nextStructure);

            if (span > 0) {
                List<GenStep> starts = new ArrayList<>();
                nextStructure.addSteps(nextPiece, starts);

                starts.forEach(next -> {
                    treeGenerate(builder, context, nextPiece, nextStructure, next, genDepth, span - 1);
                });

                return;
            }
        }
    }

    public static void generateFacility(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context, int genDepth, int span) {
        BlockPos blockPos = new BlockPos(
                context.chunkPos().getBlockX(8), 0,
                context.chunkPos().getBlockZ(8));
        blockPos = blockPos.atY(context.chunkGenerator().getBaseHeight(blockPos.getX(), blockPos.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()));

        List<GenStep> starts = new ArrayList<>();
        FacilityPiece entranceNew = ENTRANCES.findNextPiece(context.random()).orElseThrow();
        FacilityPieceInstance entrancePiece = entranceNew.createStructurePiece(context.structureManager(), genDepth);
        entrancePiece.setRotation(Direction.Plane.HORIZONTAL.getRandomDirection(context.random()));
        entrancePiece.setupBoundingBoxOnBottomCenter(blockPos);
        builder.addPiece(entrancePiece);

        entrancePiece.addSteps(entranceNew, starts);

        if (span > 0) {
            starts.forEach(start -> {
                treeGenerate(builder, context, entranceNew, entrancePiece, start, genDepth, span - 1);
            });
        }
    }
}
