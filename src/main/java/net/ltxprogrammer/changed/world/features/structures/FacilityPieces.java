package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.GluBlock;
import net.ltxprogrammer.changed.world.features.structures.facility.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.*;

public class FacilityPieces {
    public static final FacilityPiece ENTRANCE_RED = new FacilityEntrance(Zone.RED_ZONE, Changed.modResource("facility/entrance_red"));
    public static final FacilityPiece ENTRANCE_BLUE = new FacilityEntrance(Zone.BLUE_ZONE, Changed.modResource("facility/entrance_blue"));
    public static final FacilityPieceCollection ENTRANCES = FacilityPieceCollection.of(/*ENTRANCE_RED, */ENTRANCE_BLUE);

    public static final FacilityPiece STAIRCASE_START_RED = new FacilityStaircaseStart(Zone.RED_ZONE, Changed.modResource("facility/staircase_start_red"));
    public static final FacilityPiece STAIRCASE_START_BLUE = new FacilityStaircaseStart(Zone.BLUE_ZONE, Changed.modResource("facility/staircase_start_blue"));
    public static final FacilityPieceCollection STAIRCASE_STARTS = FacilityPieceCollection.of(STAIRCASE_START_RED, STAIRCASE_START_BLUE);
    public static final FacilityPiece STAIRCASE_SECTION_RED = new FacilityStaircaseSection(Zone.RED_ZONE, Changed.modResource("facility/staircase_section_red"));
    public static final FacilityPiece STAIRCASE_SECTION_BLUE = new FacilityStaircaseSection(Zone.BLUE_ZONE, Changed.modResource("facility/staircase_section_blue"));
    public static final FacilityPieceCollection STAIRCASE_SECTIONS = FacilityPieceCollection.of(STAIRCASE_SECTION_RED, STAIRCASE_SECTION_BLUE);
    public static final FacilityPiece STAIRCASE_END_RED = new FacilityStaircaseEnd(Zone.RED_ZONE, Changed.modResource("facility/staircase_end_red"));
    public static final FacilityPiece STAIRCASE_END_BLUE = new FacilityStaircaseEnd(Zone.BLUE_ZONE, Changed.modResource("facility/staircase_end_blue"));
    public static final FacilityPieceCollection STAIRCASE_ENDS = FacilityPieceCollection.of(STAIRCASE_END_RED, STAIRCASE_END_BLUE);

    public static final FacilityPiece CORRIDOR_RED = new FacilityCorridorSection(Zone.RED_ZONE, Changed.modResource("facility/corridor_red"));
    public static final FacilityPiece CORRIDOR_BLUE_V1 = new FacilityCorridorSection(Zone.BLUE_ZONE, Changed.modResource("facility/corridor_blue_v1"));
    public static final FacilityPiece CORRIDOR_BLUE_V2 = new FacilityCorridorSection(Zone.BLUE_ZONE, Changed.modResource("facility/corridor_blue_v2"));
    public static final FacilityPiece CORRIDOR_BLUE_V3 = new FacilityCorridorSection(Zone.BLUE_ZONE, Changed.modResource("facility/corridor_blue_v3"));
    public static final FacilityPiece CORRIDOR_BLUE_T_V1 = new FacilityCorridorSection(Zone.BLUE_ZONE, Changed.modResource("facility/corridor_blue_t_v1"));
    public static final FacilityPiece CORRIDOR_BLUE_TURN_V1 = new FacilityCorridorSection(Zone.BLUE_ZONE, Changed.modResource("facility/corridor_blue_turn_v1"));
    public static final FacilityPiece INTERSECTION_RED = new FacilityCorridorSection(Zone.RED_ZONE, Changed.modResource("facility/intersection_red"));
    public static final FacilityPiece LASER_HALL = new FacilityCorridorSection(Zone.RED_ZONE, Changed.modResource("facility/laser_hall"));
    public static final FacilityPieceCollection CORRIDORS = FacilityPieceCollection.of(CORRIDOR_RED,
            CORRIDOR_BLUE_V1, CORRIDOR_BLUE_V2, CORRIDOR_BLUE_V3, CORRIDOR_BLUE_T_V1, CORRIDOR_BLUE_TURN_V1, INTERSECTION_RED,
            LASER_HALL);

    public static final FacilityPieceCollection ROOMS = FacilityPieceCollection.of();

    public static final Map<PieceType, FacilityPieceCollection> BY_PIECE_TYPE = Util.make(new HashMap<>(), map -> {
        map.put(PieceType.ENTRANCE, ENTRANCES);
        map.put(PieceType.STAIRCASE_START, STAIRCASE_STARTS);
        map.put(PieceType.STAIRCASE_SECTION, STAIRCASE_SECTIONS);
        map.put(PieceType.STAIRCASE_END, STAIRCASE_ENDS);
        map.put(PieceType.CORRIDOR, CORRIDORS);
        map.put(PieceType.ROOM, ROOMS);
    });

    private static BlockPos gluNeighbor(BlockPos gluPos, BlockState gluState) {
        return gluPos.relative(gluState.getValue(GluBlock.ORIENTATION).front());
    }

    private static void treeGenerate(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context,
                                     Stack<FacilityPiece> stack, StructurePiece parentStructure,
                                     GenStep start, int genDepth, int span) {
        var parent = stack.peek();
        var genStack = new FacilityGenerationStack(stack);

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
            if (!nextStructure.setupBoundingBox(builder, start.blockInfo(), context.random())) {
                reroll--;
                continue;
            }

            var startPos = gluNeighbor(start.blockInfo().pos, start.blockInfo().state);
            stack.push(nextPiece);
            builder.addPiece(nextStructure);

            if (span > 0) {
                List<GenStep> starts = new ArrayList<>();
                nextStructure.addSteps(genStack, starts);
                starts.removeIf(next -> next.blockInfo().pos.equals(startPos));

                starts.forEach(next -> {
                    treeGenerate(builder, context, stack, nextStructure, next, genDepth, span - 1);
                });

                return;
            }

            stack.pop();
        }
    }

    public static void generateFacility(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context, int genDepth, int span) {
        BlockPos blockPos = new BlockPos(
                context.chunkPos().getBlockX(8), 0,
                context.chunkPos().getBlockZ(8));
        blockPos = blockPos.atY(context.chunkGenerator().getBaseHeight(blockPos.getX(), blockPos.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()));

        Stack<FacilityPiece> stack = new Stack<>();
        List<GenStep> starts = new ArrayList<>();
        FacilityPiece entranceNew = ENTRANCES.findNextPiece(context.random()).orElseThrow();
        FacilityPieceInstance entrancePiece = entranceNew.createStructurePiece(context.structureManager(), genDepth);
        entrancePiece.setRotation(Direction.Plane.HORIZONTAL.getRandomDirection(context.random()));
        entrancePiece.setupBoundingBoxOnBottomCenter(blockPos);

        stack.push(entranceNew);
        builder.addPiece(entrancePiece);

        entrancePiece.addSteps(new FacilityGenerationStack(stack), starts);

        if (span > 0) {
            starts.forEach(start -> {
                treeGenerate(builder, context, stack, entrancePiece, start, genDepth, span - 1);
            });
        }

        stack.pop();
    }
}
