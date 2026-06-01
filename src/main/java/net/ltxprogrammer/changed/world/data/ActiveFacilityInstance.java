package net.ltxprogrammer.changed.world.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityPieceEvent;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityZoneEntities;
import net.ltxprogrammer.changed.world.features.structures.facility.Zone;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ActiveFacilityInstance {
    /**
     * Allows for the game data to check if the instance should be loaded without reading the saved contents
     */
    public static class Header {
        public String name;
        public ChunkPos minimum;
        public ChunkPos maximum;

        public static final Codec<ChunkPos> CHUNK_POS_CODEC = Codec.INT_STREAM.comapFlatMap((stream) -> {
            return Util.fixedSize(stream, 2).map((values) -> {
                return new ChunkPos(values[0], values[1]);
            });
        }, (chunkPos) -> {
            return IntStream.of(chunkPos.x, chunkPos.z);
        }).stable();

        public static final Codec<Header> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(header -> header.name),
                CHUNK_POS_CODEC.fieldOf("minimum").forGetter(header -> header.minimum),
                CHUNK_POS_CODEC.fieldOf("maximum").forGetter(header -> header.maximum)
        ).apply(instance, Header::new));

        public Header() {}

        public Header(String name, ChunkPos minimum, ChunkPos maximum) {
            this.name = name;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public String getResourceName() {
            return String.format("%s_%s.%s_%s.%s", name,
                    minimum.x, minimum.z, maximum.x, maximum.z);
        }

        public Component getDisplayName() {
            return Component.translatable("facility.site", this.name);
        }

        public boolean readInfoFromName(String fileName) {
            try {
                var splits = fileName.split("_");
                this.name = splits[0];

                var minSplits = splits[1].split("\\.");
                minimum = new ChunkPos(Integer.parseInt(minSplits[0]), Integer.parseInt(minSplits[1]));

                var maxSplits = splits[2].split("\\.");
                maximum = new ChunkPos(Integer.parseInt(maxSplits[0]), Integer.parseInt(maxSplits[1]));
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public boolean shouldBeLoaded(ChunkSource chunkSource) {
            for (int z = minimum.z; z <= maximum.z; ++z) {
                for (int x = minimum.x; x <= maximum.x; ++x) {
                    if (chunkSource.hasChunk(x, z))
                        return true;
                }
            }

            return false;
        }

        private static final List<String> NAME_FRAGMENTS = Util.make(new ArrayList<>(24), Header::initializeNameFragments);

        private static void initializeNameFragments(List<String> list) { // This is a function to allow mixins to hook
            list.add("Alpha");
            list.add("Beta");
            list.add("Gamma");
            list.add("Delta");
            list.add("Epsilon");
            list.add("Zeta");
            list.add("Eta");
            list.add("Theta");
            list.add("Iota");
            list.add("Kappa");
            list.add("Lambda");
            list.add("Mu");
            list.add("Nu");
            list.add("Xi");
            list.add("Omicron");
            list.add("Pi");
            list.add("Rho");
            list.add("Sigma");
            list.add("Tau");
            list.add("Ipsilon");
            list.add("Phi");
            list.add("Chi");
            list.add("Psi");
            list.add("Omega");
        }

        public static String generateRandomName(RandomSource random) {
            int nameLength = random.nextInt(2, 4);
            List<String> fragments = new ArrayList<>(NAME_FRAGMENTS);
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < nameLength; ++i) {
                int index = random.nextInt(fragments.size());
                if (i > 0)
                    stringBuilder.append(" ");
                stringBuilder.append(fragments.get(index));
                fragments.remove(index);
            }

            return stringBuilder.toString();
        }

        public void initialize(BoundingBox facilitySpan, RandomSource random) {
            this.minimum = new ChunkPos(
                    SectionPos.blockToSectionCoord(facilitySpan.minX()),
                    SectionPos.blockToSectionCoord(facilitySpan.minZ())
            );
            this.maximum = new ChunkPos(
                    SectionPos.blockToSectionCoord(facilitySpan.maxX()),
                    SectionPos.blockToSectionCoord(facilitySpan.maxZ())
            );
            this.name = generateRandomName(random);
        }
    }

    public static class SpawnInfo implements WeightedEntry {
        public static class EntityInfo implements WeightedEntry {
            public final FacilityZoneEntities.EntitySpawnDefinition definition;
            public int spawnedCount;

            public static final Codec<EntityInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    FacilityZoneEntities.EntitySpawnDefinition.CODEC.fieldOf("definition").forGetter(info -> info.definition),
                    Codec.INT.fieldOf("spawnedCount").forGetter(info -> info.spawnedCount)
            ).apply(instance, EntityInfo::new));

            public EntityInfo(FacilityZoneEntities.EntitySpawnDefinition definition, int spawnedCount) {
                this.definition = definition;
                this.spawnedCount = spawnedCount;
            }

            @Override
            public Weight getWeight() {
                return Weight.of(definition.weight());
            }

            public boolean isNotExhausted() {
                return spawnedCount < definition.maximum();
            }
        }

        public final List<EntityInfo> spawns;
        public int available;

        public List<EntityInfo> getSpawns() {
            return spawns;
        }

        public static final Codec<SpawnInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(EntityInfo.CODEC).fieldOf("spawns").forGetter(SpawnInfo::getSpawns),
                Codec.INT.fieldOf("available").forGetter(info -> info.available)
        ).apply(instance, SpawnInfo::new));

        public SpawnInfo(FacilityZoneEntities.ZoneEntitiesDefinition definition) {
            this.spawns = definition.spawns().stream().map(spawnDefinition -> new EntityInfo(spawnDefinition, 0)).toList();
            this.available = definition.available();
        }

        public SpawnInfo(List<EntityInfo> spawns, int available) {
            this.spawns = spawns;
            this.available = available;
        }

        @Override
        public Weight getWeight() {
            return Weight.of(spawns.stream()
                    .filter(EntityInfo::isNotExhausted)
                    .map(EntityInfo::getWeight).reduce(0, (sum, info) -> sum + info.asInt(), Integer::sum));
        }

        public boolean isNotExhausted() {
            if (spawns.stream().noneMatch(EntityInfo::isNotExhausted))
                return false;

            return spawns.stream().filter(EntityInfo::isNotExhausted).anyMatch(info -> info.definition.cost() <= available);
        }
    }

    public static class PieceInfo {
        public final ResourceLocation pieceName;
        public final BoundingBox region;
        public final int availableSpawns;
        public int spawnedEntities;
        @Nullable private CompoundTag persistentData;
        private final List<FacilityPieceEvent> pieceEvents = new ReferenceArrayList<>();
        private List<ServerPlayer> playersInside = List.of();

        public static final Codec<PieceInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("pieceName").forGetter(info -> info.pieceName),
                BoundingBox.CODEC.fieldOf("region").forGetter(info -> info.region),
                Codec.INT.fieldOf("availableSpawns").forGetter(info -> info.availableSpawns),
                Codec.INT.fieldOf("spawnedEntities").forGetter(info -> info.spawnedEntities),
                CompoundTag.CODEC.optionalFieldOf("persistentData").forGetter(info -> {
                    var additional = new CompoundTag();
                    info.saveAdditionalData(additional);
                    if (!additional.isEmpty()) {
                        info.getOrCreatePersistentData().put("additionalData", additional);
                    }

                    return Optional.ofNullable(info.persistentData);
                })
        ).apply(instance, PieceInfo::new));

        public PieceInfo(ResourceLocation pieceName, BoundingBox region, int availableSpawns, int spawnedEntities, Optional<CompoundTag> persistentData) {
            this.pieceName = pieceName;
            this.region = region;
            this.availableSpawns = availableSpawns;
            this.spawnedEntities = spawnedEntities;
            this.persistentData = persistentData.orElse(null);

            persistentData.ifPresent(tag -> {
                var additional = tag.getCompound("additionalData");
                if (!additional.isEmpty())
                    this.readAdditionalData(additional);
            });
        }

        public PieceInfo(ResourceLocation pieceName, BoundingBox region, List<FacilityPieceEvent> events) {
            this(pieceName, region, getAvailableSpawns(region), 0, Optional.empty());
            this.pieceEvents.addAll(events);
        }

        public BoundingBox getRegion() {
            return region;
        }

        public boolean isInside(Vec3i position) {
            return region.isInside(position);
        }

        public boolean isInside(Entity entity) {
            return BlockPos.betweenClosedStream(entity.getBoundingBox()).anyMatch(this::isInside);
        }

        private static int getAvailableSpawns(BoundingBox region) {
            int mass = (region.getXSpan() - 2) * (region.getYSpan() - 2) * (region.getZSpan() - 2);
            return Mth.clamp(mass / 64, 0, 4);
        }

        public boolean isNotExhausted() {
            return spawnedEntities < availableSpawns;
        }

        public @Nullable CompoundTag getPersistentData() {
            return persistentData;
        }

        public @NotNull CompoundTag getOrCreatePersistentData() {
            if (persistentData == null)
                persistentData = new CompoundTag();
            return persistentData;
        }

        /**
         * Saves piece level data to `tag`. This function is intended for future-proofing and for mixin hooks
         * @param tag CompoundTag to save to.
         */
        public void saveAdditionalData(@NotNull CompoundTag tag) {
            if (!pieceEvents.isEmpty()) {
                ListTag eventsListTag = new ListTag();

                for (FacilityPieceEvent event : pieceEvents) {
                    eventsListTag.add(StringTag.valueOf(ChangedRegistry.FACILITY_EVENTS.getKey(event).toString()));
                }

                tag.put("events", eventsListTag);
            }
        }

        /**
         * Reads piece level data from `tag`. This function is intended for future-proofing and for mixin hooks.
         * This function WILL NOT be called if there is no additional data tag in the saved data, or if it is empty.
         * @param tag CompoundTag to read from.
         */
        public void readAdditionalData(@NotNull CompoundTag tag) {
            pieceEvents.clear();

            if (tag.contains("events")) {
                tag.getList("events", 8).stream().map(StringTag.class::cast).map(StringTag::getAsString).map(ResourceLocation::parse).forEach(key -> {
                    var event = ChangedRegistry.FACILITY_EVENTS.getValue(key);
                    if (event != null)
                        pieceEvents.add(event);
                });
            }
        }

        public boolean removeEvent(FacilityPieceEvent event) {
            return pieceEvents.remove(event);
        }

        @Override
        public String toString() {
            return pieceName.toString();
        }

        public List<ServerPlayer> getPlayersInside() {
            return List.copyOf(playersInside);
        }

        public void tick(ServerLevel level, Runnable markDirty, Zone zone, List<ServerPlayer> playersInZone) {
            if (!pieceEvents.isEmpty()) {
                List<FacilityPieceEvent> pieceEventsCopy = new ReferenceArrayList<>(pieceEvents);
                pieceEventsCopy.forEach(event -> event.onPieceTick(level, this, zone, markDirty));
            }

            List<ServerPlayer> nextPlayersInPiece = new ReferenceArrayList<>();
            playersInZone.forEach(serverPlayer -> {
                if (this.isInside(serverPlayer)) {
                    nextPlayersInPiece.add(serverPlayer);
                }
            });

            playersInside.forEach(serverPlayer -> {
                if (!nextPlayersInPiece.contains(serverPlayer))
                    this.onPlayerLeave(level, serverPlayer, zone, markDirty);
            });

            nextPlayersInPiece.forEach(serverPlayer -> {
                if (!playersInside.contains(serverPlayer))
                    this.onPlayerEnter(level, serverPlayer, zone, markDirty);
            });

            playersInside = nextPlayersInPiece;
        }

        protected void onPlayerEnter(ServerLevel level, ServerPlayer player, Zone zone, Runnable markDirty) {
            if (pieceEvents.isEmpty())
                return;

            List<FacilityPieceEvent> pieceEventsCopy = new ReferenceArrayList<>(pieceEvents);
            pieceEventsCopy.forEach(event -> event.onPlayerEnterPiece(level, player, this, zone, markDirty));
        }

        protected void onPlayerLeave(ServerLevel level, ServerPlayer player, Zone zone, Runnable markDirty) {
            if (pieceEvents.isEmpty())
                return;

            List<FacilityPieceEvent> pieceEventsCopy = new ReferenceArrayList<>(pieceEvents);
            pieceEventsCopy.forEach(event -> event.onPlayerLeavePiece(level, player, this, zone, markDirty));
        }
    }

    public static class ZoneInfo {
        public final List<SpawnInfo> spawnLists;
        public final List<PieceInfo> pieceRegions;
        public final @Nullable BoundingBox zoneRegion;
        private @Nullable CompoundTag persistentData;

        public static final Codec<ZoneInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(SpawnInfo.CODEC).fieldOf("spawnLists").forGetter(info -> info.spawnLists),
                Codec.list(PieceInfo.CODEC).fieldOf("pieceRegions").forGetter(info -> info.pieceRegions),
                CompoundTag.CODEC.optionalFieldOf("persistentData").forGetter(info -> {
                    var additional = new CompoundTag();
                    info.saveAdditionalData(additional);
                    if (!additional.isEmpty()) {
                        info.getOrCreatePersistentData().put("additionalData", additional);
                    }

                    return Optional.ofNullable(info.persistentData);
                })
        ).apply(instance, ZoneInfo::new));

        public ZoneInfo(List<SpawnInfo> spawnLists, List<PieceInfo> pieceRegions, Optional<CompoundTag> persistentData) {
            this.spawnLists = spawnLists;
            this.pieceRegions = pieceRegions;
            this.zoneRegion = BoundingBox.encapsulatingBoxes(pieceRegions.stream().map(PieceInfo::getRegion)::iterator).orElse(null);
            this.persistentData = persistentData.orElse(null);

            persistentData.ifPresent(tag -> {
                var additional = tag.getCompound("additionalData");
                if (!additional.isEmpty())
                    this.readAdditionalData(additional);
            });
        }

        public Optional<Pair<SpawnInfo, SpawnInfo.EntityInfo>> getNextSpawn(RandomSource random) {
            return WeightedRandomList.create(spawnLists.stream().filter(SpawnInfo::isNotExhausted).toList()).getRandom(random)
                    .map(info -> Pair.of(info, WeightedRandomList.create(info.getSpawns())))
                    .map(pair -> pair.mapSecond(list -> list.getRandom(random)))
                    .filter(pair -> pair.getSecond().isPresent())
                    .map(pair -> pair.mapSecond(Optional::get));
        }

        public Entity spawnRandomAt(ServerLevel level, @Nullable PieceInfo piece) {
            if (piece == null)
                return null;
            if (!piece.isNotExhausted())
                return null;

            return getNextSpawn(level.getRandom()).map(pair -> {
                var entityType = pair.getSecond().definition.entityType();
                var spawnType = SpawnPlacements.getPlacementType(pair.getSecond().definition.entityType());

                var possibleSpawns = BlockPos.betweenClosedStream(piece.region).filter(blockPos -> { // Filter out blocks that spawn our entity outside a facility zone
                    BlockPos blockPos1 = blockPos.below();
                    BlockPos blockPos2 = blockPos.above();

                    return (piece.isInside(blockPos) || this.pieceRegions.stream().anyMatch(boundingBox -> boundingBox.isInside(blockPos))) &&
                            (piece.isInside(blockPos1) || this.pieceRegions.stream().anyMatch(boundingBox -> boundingBox.isInside(blockPos1))) &&
                            (piece.isInside(blockPos2) || this.pieceRegions.stream().anyMatch(boundingBox -> boundingBox.isInside(blockPos2)));
                }).filter(blockPos -> { // Prevent unloaded chunks from loading
                    BlockPos blockPos1 = blockPos.below();
                    BlockPos blockPos2 = blockPos.above();

                    return level.isLoaded(blockPos) && level.isLoaded(blockPos1) && level.isLoaded(blockPos2);
                }).filter(blockPos -> { // Allow the entity to predicate its spawn with a spawn type
                    return spawnType.canSpawnAt(level, blockPos, entityType);
                }).map(BlockPos::new).toList();

                if (possibleSpawns.isEmpty())
                    return null;

                var entity = entityType.spawn(level, Util.getRandom(possibleSpawns, level.getRandom()), MobSpawnType.STRUCTURE);

                if (entity == null)
                    return null;

                if (entity instanceof Mob mob)
                    mob.setPersistenceRequired();

                pair.getFirst().available -= pair.getSecond().definition.cost();
                pair.getSecond().spawnedCount++;
                piece.spawnedEntities++;

                return entity;
            }).orElse(null);
        }

        public Entity spawnRandom(ServerLevel level) {
            var pieces = pieceRegions.stream().filter(PieceInfo::isNotExhausted).toList();
            if (pieces.isEmpty())
                return null;
            return spawnRandomAt(level, Util.getRandom(pieces, level.random));
        }

        public @Nullable CompoundTag getPersistentData() {
            return persistentData;
        }

        public @NotNull CompoundTag getOrCreatePersistentData() {
            if (persistentData == null)
                persistentData = new CompoundTag();
            return persistentData;
        }

        /**
         * Saves zone level data to `tag`. This function is intended for future-proofing and for mixin hooks
         * @param tag CompoundTag to save to.
         */
        public void saveAdditionalData(@NotNull CompoundTag tag) {

        }

        /**
         * Reads zone level data from `tag`. This function is intended for future-proofing and for mixin hooks.
         * This function WILL NOT be called if there is no additional data tag in the saved data, or if it is empty.
         * @param tag CompoundTag to read from.
         */
        public void readAdditionalData(@NotNull CompoundTag tag) {

        }

        public void tick(ServerLevel level, Runnable markDirty, Zone zone) {
            var entity = spawnRandom(level);
            if (entity != null)
                markDirty.run();

            if (zoneRegion == null)
                return;

            var playersInZone = level.getPlayers(serverPlayer -> {
                return BlockPos.betweenClosedStream(serverPlayer.getBoundingBox()).anyMatch(zoneRegion::isInside);
            });

            pieceRegions.forEach(info -> info.tick(level, markDirty, zone, playersInZone));
        }
    }

    private Header header;

    public void setHeader(Header header) {
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    private final Map<Zone, ZoneInfo> zoneInfos;
    private @Nullable CompoundTag persistentData;

    private boolean dirty = false;

    public static final Codec<ActiveFacilityInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ChangedRegistry.FACILITY_ZONES.get().getCodec(), ZoneInfo.CODEC).fieldOf("zoneInfos").forGetter(facility -> facility.zoneInfos),
            CompoundTag.CODEC.optionalFieldOf("persistentData").forGetter(facility -> {
                var additional = new CompoundTag();
                facility.saveAdditionalData(additional);
                if (!additional.isEmpty()) {
                    facility.getOrCreatePersistentData().put("additionalData", additional);
                }

                return Optional.ofNullable(facility.persistentData);
            })
    ).apply(instance, ActiveFacilityInstance::new));

    public ActiveFacilityInstance(Map<Zone, ZoneInfo> zoneInfos, Optional<CompoundTag> persistentData) {
        this.zoneInfos = zoneInfos;
        this.persistentData = persistentData.orElse(null);

        persistentData.ifPresent(tag -> {
            var additional = tag.getCompound("additionalData");
            if (!additional.isEmpty())
                this.readAdditionalData(additional);
        });
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean value) {
        dirty = value;
    }

    public void saveToFile(DimensionDataStorage dataStorage, BiConsumer<File, Tag> tagWriter) {
        File file = dataStorage.getDataFile("facilities/" + getHeader().getResourceName());
        CODEC.encodeStart(NbtOps.INSTANCE, this).result().ifPresent(tag -> {
            tagWriter.accept(file, tag);
            this.setDirty(false);
        });
    }

    public static Stream<File> discoverInstances(DimensionDataStorage dataStorage) {
        var files = dataStorage.getDataFile("facilities/hook").getParentFile().listFiles((dir, name) -> {
            return name.endsWith(".dat");
        });

        if (files == null)
            return Stream.empty();
        return Arrays.stream(files);
    }

    public void tick(ServerLevel level) {
        Runnable markDirty = () -> this.setDirty(true);
        zoneInfos.forEach((zone, info) -> {
            info.tick(level, markDirty, zone);
        });
    }

    public @Nullable CompoundTag getPersistentData() {
        return persistentData;
    }

    public @NotNull CompoundTag getOrCreatePersistentData() {
        if (persistentData == null)
            persistentData = new CompoundTag();
        return persistentData;
    }

    /**
     * Saves facility level data to `tag`. This function is intended for future-proofing and for mixin hooks
     * @param tag CompoundTag to save to.
     */
    public void saveAdditionalData(@NotNull CompoundTag tag) {

    }

    /**
     * Reads facility level data from `tag`. This function is intended for future-proofing and for mixin hooks.
     * This function WILL NOT be called if there is no additional data tag in the saved data, or if it is empty.
     * @param tag CompoundTag to read from.
     */
    public void readAdditionalData(@NotNull CompoundTag tag) {

    }


    public record PieceGenerationInfo(ResourceLocation pieceName, BoundingBox region, Zone zone) {}

    public List<PieceGenerationInfo> getPieceGenerationInfos() {
        List<PieceGenerationInfo> gathered = new ArrayList<>();

        zoneInfos.forEach((zone, zoneInfo) -> {
            zoneInfo.pieceRegions.forEach(pieceInfo -> {
                gathered.add(new PieceGenerationInfo(pieceInfo.pieceName, pieceInfo.region, zone));
            });
        });

        return gathered;
    }
}
