package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.GluBlock;
import net.ltxprogrammer.changed.world.features.structures.facility.*;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.*;

public class FacilityPieces {
    public static final FacilityPieceCollection ENTRANCES = new FacilityPieceCollection()
            //.register(new FacilityEntrance(Changed.modResource("facility/entrance_red")))
            .register(new FacilityEntrance(Changed.modResource("facility/entrance_blue")));

    public static final FacilityPieceCollection STAIRCASE_STARTS = new FacilityPieceCollection()
            .register(new FacilityStaircaseStart(Changed.modResource("facility/staircase_start_red")))
            .register(new FacilityStaircaseStart(Changed.modResource("facility/staircase_start_blue")));
    public static final FacilityPieceCollection STAIRCASE_SECTIONS = new FacilityPieceCollection()
            .register(new FacilityStaircaseSection(Changed.modResource("facility/staircase_section_red")))
            .register(new FacilityStaircaseSection(Changed.modResource("facility/staircase_section_blue")));
    public static final FacilityPieceCollection STAIRCASE_ENDS = new FacilityPieceCollection()
            .register(new FacilityStaircaseEnd(Changed.modResource("facility/staircase_end_red")))
            .register(new FacilityStaircaseEnd(Changed.modResource("facility/staircase_end_blue")));

    public static final FacilityPieceCollection CORRIDORS = new FacilityPieceCollection()
            //.register(new FacilityCorridorSection(Changed.modResource("facility/corridor_red")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_blue_v1")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_blue_v2")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_blue_v3")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_blue_turn_v1")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_red_v1")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_red_v2")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_red_v3")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_red_turn_v1")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_gray_v1")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_gray_v2")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_gray_v3")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_gray_turn_v1")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/corridor_maintenance_v1")))

            .register(new FacilityCorridorSection(Changed.modResource("facility/longhallway1_blue")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/longhallway1_gray")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/longhallway1_red")))

            .register(new FacilityCorridorSection(Changed.modResource("facility/longhallway2_blue")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/longhallway2_gray")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/longhallway2_red")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway1_blue")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway1_gray")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway1_red")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway2_blue")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway2_gray")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway2_red")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway3_blue")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway3_gray")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway3_red")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway4_blue")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway4_gray")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/shorthallway4_red")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/stairs1_gray")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/stairs1_red")))
            //.register(new FacilityCorridorSection(Changed.modResource("facility/intersection_red")))
            .register(new FacilityCorridorSection(Changed.modResource("facility/laser_hall")));

    public static final FacilityPieceCollection TRANSITIONS = new FacilityPieceCollection()
            .register(new FacilityTransitionSection(Changed.modResource("facility/corridor_blue_stairs_to_red")))
            .register(new FacilityTransitionSection(Changed.modResource("facility/corridor_blue_stairs_to_gray")))
            .register(new FacilityTransitionSection(Changed.modResource("facility/transition_red_to_gray")));

    public static final FacilityPieceCollection SPLITS = new FacilityPieceCollection()
            .register(new FacilitySplitSection(Changed.modResource("facility/corridor_red_t_v1")))
            .register(new FacilitySplitSection(Changed.modResource("facility/corridor_blue_t_v1")))
            .register(new FacilitySplitSection(Changed.modResource("facility/corridor_gray_t_v1")))
            .register(new FacilitySplitSection(Changed.modResource("facility/intersection1_blue")))
            .register(new FacilitySplitSection(Changed.modResource("facility/intersection1_gray")))
            .register(new FacilitySplitSection(Changed.modResource("facility/intersection1_red")))
            .register(new FacilitySplitSection(Changed.modResource("facility/intersection2_blue")))
            .register(new FacilitySplitSection(Changed.modResource("facility/intersection2_gray")))
            .register(new FacilitySplitSection(Changed.modResource("facility/intersection2_red")));

    public static final FacilityPieceCollection ROOMS = new FacilityPieceCollection()
            .register(new FacilityRoomPiece(Changed.modResource("facility/room_blue_wl_test"), LootTables.DECAYED_LAB_WL))
            .register(new FacilityRoomPiece(Changed.modResource("facility/room_red_dl_test"), LootTables.DECAYED_LAB_DL))
            .register(new FacilityRoomPiece(Changed.modResource("facility/room_gray_origin"), LootTables.DECAYED_LAB_ORIGIN))
            .register(new FacilityRoomPiece(Changed.modResource("facility/room_blue_storage"), LootTables.DECAYED_LAB_WL))
            .register(new FacilityRoomPiece(Changed.modResource("facility/room_red_storage"), LootTables.DECAYED_LAB_DL))
            .register(new FacilityRoomPiece(Changed.modResource("facility/room_gray_storage"), LootTables.DECAYED_LAB_ORIGIN));

    public static final FacilityPieceCollection SEALS = new FacilityPieceCollection()
            .register(new FacilitySealPiece(Changed.modResource("facility/seal_blue")))
            .register(new FacilitySealPiece(Changed.modResource("facility/seal_red")))
            .register(new FacilitySealPiece(Changed.modResource("facility/seal_gray")));

    public static final Map<PieceType, FacilityPieceCollection> BY_PIECE_TYPE = Util.make(new HashMap<>(), map -> {
        map.put(PieceType.ENTRANCE, ENTRANCES);
        map.put(PieceType.STAIRCASE_START, STAIRCASE_STARTS);
        map.put(PieceType.STAIRCASE_SECTION, STAIRCASE_SECTIONS);
        map.put(PieceType.STAIRCASE_END, STAIRCASE_ENDS);
        map.put(PieceType.CORRIDOR, CORRIDORS);
        map.put(PieceType.SPLIT, SPLITS);
        map.put(PieceType.TRANSITION, TRANSITIONS);
        map.put(PieceType.ROOM, ROOMS);
        map.put(PieceType.SEAL, SEALS);
    });

    private static BlockPos gluNeighbor(BlockPos gluPos, BlockState gluState) {
        return gluPos.relative(gluState.getValue(GluBlock.ORIENTATION).front());
    }
    
    public static boolean isNotCompletelyInsideRegion(BoundingBox boundingBox, BoundingBox region) {
        return boundingBox.minX() < region.minX() || boundingBox.minY() < region.minY() || boundingBox.minZ() < region.minZ() ||
                boundingBox.maxX() > region.maxX() || boundingBox.maxY() > region.maxY() || boundingBox.maxZ() > region.maxZ();
    }

    private static void treeGenerate(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context,
                                     Stack<FacilityPiece> stack, StructurePiece parentStructure,
                                     GenStep start, int genDepth, int span, BoundingBox allowedRegion) {
        var parent = stack.peek();

        int reroll = 10;
        while (reroll > 0) {
            PieceType pieceType;
            BoundingBox allowedRegionForPiece = allowedRegion;
            if (parent.type == PieceType.SPLIT && reroll == 1) { // Split pieces will dead-end if it's too close to the gen region
                pieceType = PieceType.SEAL;
                allowedRegionForPiece = BoundingBox.infinite();
            } else if (span == 0) {
                pieceType = PieceType.ROOM;
            } else {
                var type = start.validTypes().getRandom(context.random());
                if (type.isEmpty())
                    return;
                pieceType = type.get().getData();
            }

            var pieces = new ArrayList<>(BY_PIECE_TYPE.get(pieceType));
            Collections.shuffle(pieces, context.random());
            for (FacilityPiece nextPiece : pieces) {
                var nextStructure = nextPiece.createStructurePiece(context.structureManager(), genDepth);
                if (!nextStructure.setupBoundingBox(builder, start.blockInfo(), context.random(), allowedRegionForPiece))
                    continue;

                var startPos = gluNeighbor(start.blockInfo().pos, start.blockInfo().state);
                builder.addPiece(nextStructure);

                int nextSpan = pieceType.shouldConsumeSpan() ? span - 1 : span;
                if (span > 0) {
                    stack.push(nextPiece);

                    var genStack = new FacilityGenerationStack(stack, nextStructure.getBoundingBox(), context, nextSpan);
                    List<GenStep> starts = new ArrayList<>();
                    nextStructure.addSteps(genStack, starts);

                    int piecesBefore = ((StructurePiecesBuilderExtender)builder).pieceCount();
                    starts.stream().filter(next -> !next.blockInfo().pos.equals(startPos)).forEach(next -> {
                        treeGenerate(builder, context, stack, nextStructure, next, genDepth, nextSpan, allowedRegion);
                    });
                    int piecesAfter = ((StructurePiecesBuilderExtender)builder).pieceCount();

                    if (piecesAfter > piecesBefore) // Successfully generated pieces that attach to this one
                        return;

                    // No piece was generated to attach to this one
                    if (pieceType == PieceType.ROOM)
                        return; // This behaviour is expected for a room

                    // Attempt to regenerate this piece as a room, to prevent a dead end
                    ((StructurePiecesBuilderExtender)builder).removePiece(nextStructure);
                    StructurePiece pieceToPut = nextStructure;
                    for (FacilityPiece nextRoom : ROOMS) {
                        var nextRoomStructure = nextRoom.createStructurePiece(context.structureManager(), genDepth);
                        if (!nextRoomStructure.setupBoundingBox(builder, start.blockInfo(), context.random(), allowedRegion))
                            continue;

                        // Success
                        pieceToPut = nextRoomStructure;
                        break;
                    }

                    if (pieceToPut == nextStructure)
                        Changed.LOGGER.debug("Failed to seal dead end in facility, startPos {}", startPos);
                    else
                        Changed.LOGGER.debug("Sealed dead end in facility, startPos {}", startPos);
                    builder.addPiece(pieceToPut);

                    stack.pop();
                    return;
                }
            }

            reroll--;
        }

        return;
    }

    public static void generateFacility(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context, int genDepth, int span, BoundingBox allowedRegion) {
        BlockPos blockPos = new BlockPos(
                context.chunkPos().getBlockX(8), 0,
                context.chunkPos().getBlockZ(8));
        blockPos = blockPos.atY(context.chunkGenerator().getBaseHeight(blockPos.getX(), blockPos.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()));

        Stack<FacilityPiece> stack = new Stack<>();
        List<GenStep> starts = new ArrayList<>();
        FacilityPiece entranceNew = ENTRANCES.findNextPiece(context.random()).orElseThrow();
        FacilityPieceInstance entrancePiece = entranceNew.createStructurePiece(context.structureManager(), genDepth);

        var directions = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
        Collections.shuffle(directions, context.random());

        for (Direction dir : directions) {
            entrancePiece.setRotation(dir);
            entrancePiece.setupBoundingBoxOnBottomCenter(blockPos);
            BoundingBox entranceBB = entrancePiece.getBoundingBox();

            int minXminZ = context.chunkGenerator().getBaseHeight(entranceBB.minX() + 1, entranceBB.minZ() + 1, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
            int minXmaxZ = context.chunkGenerator().getBaseHeight(entranceBB.minX() + 1, entranceBB.maxZ() - 1, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
            int maxXminZ = context.chunkGenerator().getBaseHeight(entranceBB.maxX() - 1, entranceBB.minZ() + 1, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
            int maxXmaxZ = context.chunkGenerator().getBaseHeight(entranceBB.maxX() - 1, entranceBB.maxZ() - 1, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
            int min = Math.min(Math.min(minXminZ, minXmaxZ), Math.min(maxXminZ, maxXmaxZ));
            int max = Math.max(Math.max(minXminZ, minXmaxZ), Math.max(maxXminZ, maxXmaxZ));

            entrancePiece.setupBoundingBoxOnBottomCenter(new BlockPos(blockPos.getX(), min, blockPos.getZ()));

            if (max - min < 3) break; // Surface is flat enough to not worry about rotating the entrance

            BlockPos testPos = entrancePiece.getRandomStart(context.random());
            double minX = Mth.lerp((double)(testPos.getZ() - entranceBB.minZ()) / (double)entranceBB.getZSpan(), (double)minXminZ, (double)minXmaxZ);
            double maxX = Mth.lerp((double)(testPos.getZ() - entranceBB.minZ()) / (double)entranceBB.getZSpan(), (double)maxXminZ, (double)maxXmaxZ);
            double height = Mth.lerp((double)(testPos.getX() - entranceBB.minX()) / (double)entranceBB.getXSpan(), minX, maxX);

            if (testPos.getY() < height) break; // Next structure piece is in the surface
        }

        stack.push(entranceNew);
        builder.addPiece(entrancePiece);

        entrancePiece.addSteps(new FacilityGenerationStack(stack, entrancePiece.getBoundingBox(), context, span), starts);

        if (span > 0) {
            starts.forEach(start -> {
                treeGenerate(builder, context, stack, entrancePiece, start, genDepth, span - 1, allowedRegion);
            });
        }

        stack.pop();
    }
}
