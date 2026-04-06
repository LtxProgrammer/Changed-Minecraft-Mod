package net.ltxprogrammer.changed.world.features.structures;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.GluBlock;
import net.ltxprogrammer.changed.init.ChangedFacilityPieceTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.ltxprogrammer.changed.util.StreamUtil;
import net.ltxprogrammer.changed.world.features.structures.facility.*;
import net.ltxprogrammer.changed.world.features.structures.facility.types.PieceType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FacilityPieces extends SimplePreparableReloadListener<Set<ConfiguredFacilityPiece>> {
    public static FacilityPieces INSTANCE = new FacilityPieces();
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<PieceType<?>, FacilityPieceCollection> facilityPieceCollections = new HashMap<>();
    private final Set<Zone> zonesWithDefinedPieces = new HashSet<>();

    private ConfiguredFacilityPiece processJSONFile(JsonObject root) {
        return ConfiguredFacilityPiece.CODEC.decode(JsonOps.INSTANCE, root)
                .getOrThrow(false, error -> { throw new RuntimeException(error); }).getFirst();
    }

    @Override
    @NotNull
    public Set<ConfiguredFacilityPiece> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        return ResourceUtil.processJSONResources(new HashSet<>(), resources, "worldgen/changed/facility", (list, filename, id, json) -> {
            var configured = processJSONFile(json).setName(id);
            if (configured.minimum > 0 && configured.facilityPiece.getType() != ChangedFacilityPieceTypes.ROOM.get())
                LOGGER.warn("Facility piece {} has a nonzero minimum place count, but is not supported for its type: {}", id, configured.facilityPiece.getType());
            list.add(configured);
        }, (exception, filename) -> LOGGER.error("Failed to load facility piece configuration from \"{}\" : {}", filename, exception));
    }

    @Override
    protected void apply(@NotNull Set<ConfiguredFacilityPiece> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        facilityPieceCollections.clear();
        zonesWithDefinedPieces.clear();

        for (var pieceType : ChangedRegistry.FACILITY_PIECE_TYPES.get().getValues()) {
            FacilityPieceCollectionBuilder builder = new FacilityPieceCollectionBuilder();
            output.stream().filter(piece -> piece.getFacilityPiece().getType() == pieceType).forEach(builder::register);

            Changed.postModLoadingEvent(new GatherFacilityPiecesEvent(pieceType, builder));

            facilityPieceCollections.put(pieceType, builder.build());
            facilityPieceCollections.get(pieceType).stream()
                    .map(ConfiguredFacilityPiece::getConnectsTo)
                    .flatMap(Set::stream)
                    .forEach(zonesWithDefinedPieces::add);
        }
    }
    
    public static class PlacedFacilityPiece {
        private final Zone zone;
        private final ConfiguredFacilityPiece definition;
        private final FacilityPieceInstance instance;
        PlacedFacilityPiece parent;

        public PlacedFacilityPiece(Zone zone, ConfiguredFacilityPiece definition, FacilityPieceInstance instance) {
            this.zone = zone;
            this.definition = definition;
            this.instance = instance;
        }

        public Zone getZone() {
            return zone;
        }

        public ConfiguredFacilityPiece getDefinition() {
            return definition;
        }

        public FacilityPieceInstance getInstance() {
            return instance;
        }

        public void setParent(PlacedFacilityPiece placed) {
            this.parent = placed;
        }

        public PlacedFacilityPiece getParent() {
            return parent;
        }
    }

    public static FacilityPieceCollection getPiecesOfType(PieceType<?> pieceType) {
        return INSTANCE.facilityPieceCollections.get(pieceType);
    }
    
    public static boolean isNotCompletelyInsideRegion(BoundingBox boundingBox, BoundingBox region) {
        return boundingBox.minX() < region.minX() || boundingBox.minY() < region.minY() || boundingBox.minZ() < region.minZ() ||
                boundingBox.maxX() > region.maxX() || boundingBox.maxY() > region.maxY() || boundingBox.maxZ() > region.maxZ();
    }

    private static Predicate<ConfiguredFacilityPiece> pieceConnectsToZone(Zone zone) {
        return configuredFacilityPiece -> {
            return configuredFacilityPiece.getConnectsTo().isEmpty() || configuredFacilityPiece.getConnectsTo().contains(zone);
        };
    }

    private static Predicate<ConfiguredFacilityPiece> pieceConnectsToZones(Zone zone, Zone wantedZone) {
        if (zone == wantedZone)
            return pieceConnectsToZone(zone);

        return configuredFacilityPiece -> {
            return configuredFacilityPiece.getConnectsTo().isEmpty() || (
                    configuredFacilityPiece.getConnectsTo().contains(zone) && configuredFacilityPiece.getConnectsTo().contains(wantedZone));
        };
    }

    private static Predicate<ConfiguredFacilityPiece> meetsPiecePositionRequirements(FacilityGenerationContext context, BoundingBox region) {
        final var center = region.getCenter();
        final var surfaceBiomes = context.structureContext.biomeSource().getBiomesWithin(
                center.getX(), context.structureContext.chunkGenerator().getSeaLevel(), center.getZ(), /* Radius */ 8,
                context.structureContext.randomState().sampler());

        return configuredFacilityPiece -> {
            return surfaceBiomes.stream().anyMatch(configuredFacilityPiece.surfaceBiomePredicate::testHolder);
        };
    }

    private static Predicate<Zone> meetsZonePositionRequirements(FacilityGenerationContext context, BoundingBox region) {
        final var center = region.getCenter();
        final var surfaceBiomes = context.structureContext.biomeSource().getBiomesWithin(
                center.getX(), context.structureContext.chunkGenerator().getSeaLevel(), center.getZ(), /* Radius */ 8,
                context.structureContext.randomState().sampler());
        return zone -> {
            return surfaceBiomes.stream().anyMatch(zone.getSurfaceBiomePredicate()::testHolder);
        };
    }

    private static Predicate<ConfiguredFacilityPiece> hasNotReachedMaximum(FacilityGenerationContext facilityContext) {
        return nextConfiguredPiece -> {
            if (nextConfiguredPiece.facilityPiece.getType() == ChangedFacilityPieceTypes.SEAL.get())
                return true;
            return facilityContext.configuredPieceCounts.getOrDefault(nextConfiguredPiece, 0) < nextConfiguredPiece.getMaximum();
        };
    }

    public static class FacilityGenerationContext {
        public final StructurePiecesBuilder builder;
        public final Structure.GenerationContext structureContext;
        public final Map<ConfiguredFacilityPiece, Integer> configuredPieceCounts = new HashMap<>();
        public final Map<Zone, List<PlacedFacilityPiece>> piecesByZone = new HashMap<>();
        public final Multimap<PlacedFacilityPiece, PlacedFacilityPiece> pieceDependents = HashMultimap.create();

        public FacilityGenerationContext(StructurePiecesBuilder builder, Structure.GenerationContext structureContext) {
            this.builder = builder;
            this.structureContext = structureContext;
        }

        public Stream<Zone> getRemainingZonesToGenerate(Zone currentZone) {
            return INSTANCE.zonesWithDefinedPieces.stream().filter(zone -> {
                return (!zone.isUnique() || !piecesByZone.containsKey(zone) || piecesByZone.get(zone).isEmpty()) && currentZone != zone;
            });
        }

        private void removeReferencesTo(PlacedFacilityPiece piece) {
            var extender = ((StructurePiecesBuilderExtender)builder);
            extender.removePiece(piece.instance);
            pieceDependents.removeAll(piece);

            configuredPieceCounts.put(piece.definition, configuredPieceCounts.get(piece.definition) - 1);

            piecesByZone.forEach((zone, list) -> {
                list.removeIf(placed -> placed.instance == piece.instance);
            });
        }

        public void removePieceAndDependents(PlacedFacilityPiece placed) {
            var dependents = new ArrayList<>(pieceDependents.get(placed));
            dependents.forEach(this::removeReferencesTo);

            this.removeReferencesTo(placed);
        }

        public void addPiece(PlacedFacilityPiece placed) {
            builder.addPiece(placed.instance);
            configuredPieceCounts.compute(placed.definition, (configuredPiece, count) -> {
                if (count == null)
                    return 1;
                return count + 1;
            });

            piecesByZone.computeIfAbsent(placed.zone, toMap -> new ArrayList<>()).add(placed);
        }

        public void registerDependents(PlacedFacilityPiece placed, Set<PlacedFacilityPiece> directDependents) {
            pieceDependents.putAll(placed, directDependents);
            directDependents.forEach(directDependent -> {
                pieceDependents.putAll(placed, pieceDependents.get(directDependent));
            });
        }
    }

    private static int sequentialMatch(Stack<ConfiguredFacilityPiece> stack, Predicate<ConfiguredFacilityPiece> predicate, boolean includingNonSpan) {
        int nonSpan = 0;
        for (int i = stack.size() - 1; i >= 0; --i) {
            var element = stack.elementAt(i);
            if (!includingNonSpan && !element.getFacilityPiece().getType().shouldConsumeSpan()) {
                nonSpan++;
                continue;
            }
            if (!predicate.test(element))
                return stack.size() - (i + 1) - nonSpan;
        }

        return stack.size() - nonSpan;
    }

    private static Predicate<ConfiguredFacilityPiece> limitAttempts(PieceType<?> pieceType, Zone zone, int maximumCount) {
        AtomicInteger countedPieces = new AtomicInteger(0);
        AtomicBoolean logEvent = new AtomicBoolean(true);
        return configuredFacilityPiece -> {
            boolean result = countedPieces.getAndIncrement() < maximumCount;
            if (!result && logEvent.getAndSet(false))
                Changed.LOGGER.debug("Hit maximum attempts ({}) for piece type {} in zone {} while placing {}",
                        maximumCount, pieceType, zone, configuredFacilityPiece.getName());
            return result;
        };
    }

    private static Optional<PlacedFacilityPiece> treeGenerate(FacilityGenerationContext facilityContext,
                                     Stack<ConfiguredFacilityPiece> stack, FacilityPieceInstance parentStructure,
                                     GenStep start, int genDepth, BoundingBox allowedRegion, int zoneProtection) {
        { // Early out for common failures
            final var center = parentStructure.getBoundingBox().getCenter();
            if (center.getY() > facilityContext.structureContext.chunkGenerator().getSeaLevel() - 30) {
                final var pieceBiomes = facilityContext.structureContext.biomeSource().getBiomesWithin(
                        center.getX(), center.getY(), center.getZ(), /* Radius */ 2,
                        facilityContext.structureContext.randomState().sampler());
                if (pieceBiomes.stream().anyMatch(pieceBiome -> pieceBiome.is(ChangedTags.Biomes.DENY_FACILITY_PLACEMENT)))
                    return Optional.empty();
            }
        }

        final var random = facilityContext.structureContext.random();
        var configuredParent = stack.peek();
        var parent = configuredParent.getFacilityPiece();
        var zone = start.getZone();

        int zoneLength = sequentialMatch(stack, configuredPiece -> {
            return configuredPiece.getConnectsTo().isEmpty() || configuredPiece.getConnectsTo().contains(zone);
        }, false);

        Stream<PieceType<?>> pieceTypeStream = StreamUtil.weightedShuffledStream(start.validTypes(), random)
                .map(WeightedPieceNeighborSupplier::getPieceType);

        if ((parent.type == ChangedFacilityPieceTypes.STAIRCASE_START.get() ||
                parent.type == ChangedFacilityPieceTypes.STAIRCASE_SECTION.get())) {
            pieceTypeStream = Stream.concat(pieceTypeStream,
                    Stream.of(ChangedFacilityPieceTypes.STAIRCASE_END.get()));
        }

        else if (genDepth <= 0) {
            pieceTypeStream = Stream.of(ChangedFacilityPieceTypes.ROOM.get(), ChangedFacilityPieceTypes.SEAL.get());
        }

        Stream<Pair<PieceType<?>, Zone>> pieceZoneStream = pieceTypeStream.mapMulti((pieceType, sink) -> {
            if (zoneProtection <= 0 && zone != null && zoneLength > zone.getMinimumLength() &&
                    pieceType.canBeReplacedBy(ChangedFacilityPieceTypes.TRANSITION.get())) {
                // Coerce the next piece to be a transition to another zone
                int zoneDelta = zoneLength - zone.getMinimumLength();

                if (random.nextInt(4) < zoneDelta) {
                    // Queue transitions first
                    facilityContext.getRemainingZonesToGenerate(zone)
                            .filter(meetsZonePositionRequirements(facilityContext, parentStructure.getBoundingBox()))
                            .forEach(nextZone -> {
                        sink.accept(Pair.of(ChangedFacilityPieceTypes.TRANSITION.get(), nextZone));
                    });

                    // Queue fallback last
                    sink.accept(Pair.of(pieceType, zone));
                }

                else {
                    // Queue fallback first
                    sink.accept(Pair.of(pieceType, zone));

                    // Queue transitions last
                    facilityContext.getRemainingZonesToGenerate(zone)
                            .filter(meetsZonePositionRequirements(facilityContext, parentStructure.getBoundingBox()))
                            .forEach(nextZone -> {
                        sink.accept(Pair.of(ChangedFacilityPieceTypes.TRANSITION.get(), nextZone));
                    });
                }
            }

            else {
                sink.accept(Pair.of(pieceType, zone));
            }
        });

        Stream<PlacedFacilityPiece> placedPieceStream = pieceZoneStream.flatMap(pair -> {
            final PieceType<?> pieceType = pair.getFirst();
            final Zone nextZone = pair.getSecond();
            return INSTANCE.facilityPieceCollections.get(pieceType).shuffledStream(random)
                    .filter(hasNotReachedMaximum(facilityContext)
                            .and(pieceConnectsToZones(zone, nextZone))
                            .and(meetsPiecePositionRequirements(facilityContext, parentStructure.getBoundingBox()))
                            .and(limitAttempts(pieceType, nextZone, Changed.config.server.facilityPlacementAttemptsPerPieceType.get())))
                    .map(nextConfiguredPiece -> {
                        var nextPiece = nextConfiguredPiece.getFacilityPiece();
                        var nextStructure = nextPiece.createStructurePiece(facilityContext.structureContext.structureTemplateManager(), genDepth);
                        if (!nextStructure.setupBoundingBox(facilityContext.builder, start.blockInfo(), random, allowedRegion))
                            return null;

                        var placed = new PlacedFacilityPiece(nextZone, nextConfiguredPiece, nextStructure);

                        var startPos = GluBlock.getConnection(start.blockInfo().pos(), start.blockInfo().state());
                        facilityContext.addPiece(placed);

                        int nextSpan = pieceType.shouldConsumeSpan() ? genDepth - 1 : genDepth;
                        stack.push(nextConfiguredPiece);

                        var genStack = new FacilityGenerationStack(stack, nextStructure.getBoundingBox(), facilityContext.structureContext, nextSpan);
                        ObjectArrayList<GenStep> starts = new ObjectArrayList<>();
                        nextStructure.addSteps(genStack, starts);
                        Util.shuffle(starts, random);

                        boolean firstStart = true;
                        Set<PlacedFacilityPiece> directDependents = new HashSet<>();
                        for (var next : starts) {
                            if (next.blockInfo().pos().equals(startPos))
                                continue;
                            var connectorPos = GluBlock.getConnection(next.blockInfo().pos(), next.blockInfo().state());
                            if (parentStructure.getBoundingBox().isInside(connectorPos))
                                continue;
                            if (directDependents.stream().map(placedDependent -> placedDependent.instance.getBoundingBox())
                                    .anyMatch(box -> box.isInside(connectorPos)))
                                continue;

                            var childRoom = treeGenerate(facilityContext, stack, nextStructure, next,
                                    firstStart ? nextSpan : nextSpan - 5,
                                    allowedRegion,
                                    firstStart ? Math.max(zoneProtection - 1, 0) : 5);
                            if (childRoom.isPresent()) {
                                firstStart = false;
                            }

                            childRoom.ifPresent(placedChild -> {
                                directDependents.add(placedChild);
                                placedChild.setParent(placed);
                            });
                        }

                        stack.pop();

                        facilityContext.registerDependents(placed, directDependents);
                        if (!nextPiece.isValidGeneration(new PlacedFacilityPiece(zone, configuredParent, parentStructure), directDependents)) {
                            LOGGER.debug("{} denied generation with {} direct dependent(s)", placed.definition.getName(), directDependents.size());
                            facilityContext.removePieceAndDependents(placed);
                            return null;
                        }

                        return placed;
                    }).filter(Objects::nonNull);
        });

        return placedPieceStream.findFirst();
    }

    private static Optional<PlacedFacilityPiece> tryReplaceRoom(FacilityGenerationContext facilityContext, ConfiguredFacilityPiece requiredPiece,
                                                                BoundingBox allowedRegion) {
        final var random = facilityContext.structureContext.random();

        List<PlacedFacilityPiece> replaceableRooms = facilityContext.piecesByZone.entrySet().stream().filter(entry -> {
            return requiredPiece.connectsTo.isEmpty() || requiredPiece.connectsTo.contains(entry.getKey());
        }).flatMap(entry -> entry.getValue().stream()).filter(placedPiece -> {
            return facilityContext.pieceDependents.get(placedPiece).isEmpty() &&
                facilityContext.configuredPieceCounts.get(placedPiece.definition) > placedPiece.definition.minimum;
        }).filter(placedPiece -> {
            return meetsPiecePositionRequirements(facilityContext, placedPiece.parent.instance.getBoundingBox()).test(requiredPiece);
        }).toList(); // Collect so that the next stage can modify piecesByZone

        Stream<PlacedFacilityPiece> replacedRoomStream = replaceableRooms.stream().map(replacingPiece -> {
            var parent = replacingPiece.getParent();

            List<GenStep> starts = new ArrayList<>();
            parent.instance.addSteps(null, starts);
            if (starts.isEmpty())
                return null;

            facilityContext.removePieceAndDependents(replacingPiece);

            var nextPiece = requiredPiece.getFacilityPiece();
            var nextStructure = nextPiece.createStructurePiece(facilityContext.structureContext.structureTemplateManager(), 0);

            for (var start : starts) {
                if (!nextStructure.setupBoundingBox(facilityContext.builder, start.blockInfo(), random, allowedRegion))
                    continue;

                // Found a generation point
                var placed = new PlacedFacilityPiece(start.getZone(), requiredPiece, nextStructure);
                placed.setParent(parent);
                facilityContext.addPiece(placed);
                facilityContext.registerDependents(parent, Set.of(placed));
                return placed;
            }

            // No generation point found, return piece into collection
            facilityContext.addPiece(replacingPiece);
            facilityContext.registerDependents(parent, Set.of(replacingPiece));
            return null;
        }).filter(Objects::nonNull);

        return replacedRoomStream.findFirst();
    }

    private static Optional<PlacedFacilityPiece> tryPlaceEntrance(FacilityGenerationContext facilityContext, int genDepth, BlockPos blockPos) {
        Optional<ConfiguredFacilityPiece> entranceNewOpt = INSTANCE.facilityPieceCollections.get(ChangedFacilityPieceTypes.ENTRANCE.get()).shuffledStream(facilityContext.structureContext.random())
                .filter(meetsPiecePositionRequirements(facilityContext, new BoundingBox(blockPos).inflatedBy(7))).findFirst();
        if (entranceNewOpt.isEmpty())
            return Optional.empty();

        ConfiguredFacilityPiece entranceNew = entranceNewOpt.get();
        FacilityPieceInstance entrancePiece = entranceNew.getFacilityPiece().createStructurePiece(facilityContext.structureContext.structureTemplateManager(), genDepth);

        var directions = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
        CollectionUtil.shuffle(directions, facilityContext.structureContext.random());

        for (Direction dir : directions) {
            entrancePiece.setRotation(dir);
            entrancePiece.setupBoundingBoxOnBottomCenter(blockPos);
            BoundingBox entranceBB = entrancePiece.getBoundingBox();

            int minXminZ = facilityContext.structureContext.chunkGenerator().getBaseHeight(entranceBB.minX() + 1, entranceBB.minZ() + 1, Heightmap.Types.WORLD_SURFACE_WG, facilityContext.structureContext.heightAccessor(), facilityContext.structureContext.randomState());
            int minXmaxZ = facilityContext.structureContext.chunkGenerator().getBaseHeight(entranceBB.minX() + 1, entranceBB.maxZ() - 1, Heightmap.Types.WORLD_SURFACE_WG, facilityContext.structureContext.heightAccessor(), facilityContext.structureContext.randomState());
            int maxXminZ = facilityContext.structureContext.chunkGenerator().getBaseHeight(entranceBB.maxX() - 1, entranceBB.minZ() + 1, Heightmap.Types.WORLD_SURFACE_WG, facilityContext.structureContext.heightAccessor(), facilityContext.structureContext.randomState());
            int maxXmaxZ = facilityContext.structureContext.chunkGenerator().getBaseHeight(entranceBB.maxX() - 1, entranceBB.maxZ() - 1, Heightmap.Types.WORLD_SURFACE_WG, facilityContext.structureContext.heightAccessor(), facilityContext.structureContext.randomState());
            int min = Math.min(Math.min(minXminZ, minXmaxZ), Math.min(maxXminZ, maxXmaxZ));
            int max = Math.max(Math.max(minXminZ, minXmaxZ), Math.max(maxXminZ, maxXmaxZ));

            entrancePiece.setupBoundingBoxOnBottomCenter(new BlockPos(blockPos.getX(), min, blockPos.getZ()));

            if (max - min < 3) break; // Surface is flat enough to not worry about rotating the entrance

            BlockPos testPos = entrancePiece.getRandomStart(facilityContext.structureContext.random());
            double minX = Mth.lerp((double)(testPos.getZ() - entranceBB.minZ()) / (double)entranceBB.getZSpan(), (double)minXminZ, (double)minXmaxZ);
            double maxX = Mth.lerp((double)(testPos.getZ() - entranceBB.minZ()) / (double)entranceBB.getZSpan(), (double)maxXminZ, (double)maxXmaxZ);
            double height = Mth.lerp((double)(testPos.getX() - entranceBB.minX()) / (double)entranceBB.getXSpan(), minX, maxX);

            if (testPos.getY() < height) break; // Next structure piece is in the surface
        }

        var zone = entrancePiece.getGluBlocks().stream().map(info -> GluBlock.getZone(info.nbt())).findAny();
        if (zone.isEmpty()) {
            Changed.LOGGER.warn("Facility entrance piece {} is missing glu blocks", entranceNew.getName());
            return Optional.empty();
        }

        return Optional.of(new PlacedFacilityPiece(zone.get(), entranceNew, entrancePiece));
    }

    public static Optional<FacilityKeystone> generateFacility(StructurePiecesBuilder builder, Structure.GenerationContext context, int genDepth, BoundingBox allowedRegion) {
        BlockPos chunkCenter = context.chunkPos().getMiddleBlockPosition(0);
        BlockPos surfacePos = chunkCenter.atY(context.chunkGenerator().getBaseHeight(chunkCenter.getX(), chunkCenter.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()));

        var facilityGenerationContext = new FacilityGenerationContext(builder, context);
        var entranceOpt = tryPlaceEntrance(facilityGenerationContext, genDepth, surfacePos);
        if (entranceOpt.isEmpty())
            return Optional.empty();
        var entrance = entranceOpt.get();

        facilityGenerationContext.addPiece(entrance);

        Stack<ConfiguredFacilityPiece> stack = new Stack<>();
        List<GenStep> starts = new ArrayList<>();

        stack.push(entrance.definition);
        entrance.instance.addSteps(new FacilityGenerationStack(stack, entrance.instance.getBoundingBox(), context, genDepth), starts);

        Set<PlacedFacilityPiece> directDependents = new HashSet<>();
        if (genDepth > 0) {
            starts.forEach(start -> {
                treeGenerate(facilityGenerationContext, stack, entrance.instance, start, genDepth - 1, allowedRegion, 0)
                        .ifPresent(directDependents::add);
            });
        }

        stack.pop();

        if (directDependents.isEmpty()) {
            LOGGER.debug("Skipping entrance that failed to generate neighbors");
            return Optional.empty();
        }

        var requiredMap = INSTANCE.facilityPieceCollections.values().stream().flatMap(collection -> collection.shuffledStream(context.random()))
                .filter(piece -> piece.getFacilityPiece().getType() == ChangedFacilityPieceTypes.ROOM.get()) // Rooms only, for now
                .filter(piece -> piece.getMinimum() > facilityGenerationContext.configuredPieceCounts.getOrDefault(piece, 0))
                .map(configured -> Pair.of(configured, configured.getMinimum() - facilityGenerationContext.configuredPieceCounts.getOrDefault(configured, 0)))
                .toList();

        StreamUtil.shuffledStream(requiredMap, context.random()).forEach(pair -> {
            ConfiguredFacilityPiece requiredPiece = pair.getFirst();
            int count = pair.getSecond();
            LOGGER.debug("Attempting to put {} count of {} in generating facility", count, requiredPiece.getName());
            while (count-- > 0) {
                var forcedRoom = tryReplaceRoom(facilityGenerationContext, requiredPiece, allowedRegion);
                if (forcedRoom.isPresent())
                    LOGGER.debug("Successfully inserted {} into facility", requiredPiece.getName());
                else {
                    LOGGER.debug("Failed to insert {} into facility, skipping remainder", requiredPiece.getName());
                    break;
                }
            }
        });

        Map<Zone, List<Pair<ResourceLocation, BoundingBox>>> zoneBoundingBoxes = new HashMap<>();
        facilityGenerationContext.piecesByZone.forEach((zone, pieces) -> {
            zoneBoundingBoxes.put(zone, pieces.stream().map(pair ->
                    Pair.of(pair.definition.getName(), pair.instance.getBoundingBox())).toList());
        });

        try {
            return Optional.of(new FacilityKeystone(genDepth, zoneBoundingBoxes, entrance.instance.getBoundingBox(), context.random()));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
