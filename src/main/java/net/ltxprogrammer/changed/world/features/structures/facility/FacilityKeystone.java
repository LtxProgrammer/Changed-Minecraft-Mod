package net.ltxprogrammer.changed.world.features.structures.facility;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedStructurePieceTypes;
import net.ltxprogrammer.changed.world.data.ActiveFacilityInstance;
import net.ltxprogrammer.changed.world.data.ChangedGameDataAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Meta piece that holds all the properties of the facility
 * Does not place any blocks
 * Is used to create a facility data object to handle spawns and facility operations
 */
public class FacilityKeystone extends StructurePiece {
    private ActiveFacilityInstance.Header header;
    private Map<Zone, List<Pair<ResourceLocation, BoundingBox>>> piecesByZone;

    private static final Codec<Pair<ResourceLocation, BoundingBox>> PIECE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("name").forGetter(Pair::getFirst),
            BoundingBox.CODEC.fieldOf("region").forGetter(Pair::getSecond)
    ).apply(instance, Pair::of));

    private static final Codec<Map<Zone, List<Pair<ResourceLocation, BoundingBox>>>> PIECES_BY_ZONE_CODEC = Codec.unboundedMap(
            ChangedRegistry.FACILITY_ZONES.get().getCodec(),
            Codec.list(PIECE_CODEC)
    );

    public FacilityKeystone(int genDepth, Map<Zone, List<Pair<ResourceLocation, BoundingBox>>> piecesByZone, BoundingBox entrance, RandomSource random) {
        super(ChangedStructurePieceTypes.FACILITY_KEYSTONE.get(), genDepth, entrance);

        this.piecesByZone = piecesByZone;
        header = new ActiveFacilityInstance.Header();
        header.initialize(BoundingBox.encapsulatingBoxes(piecesByZone.values().stream().flatMap(List::stream).map(Pair::getSecond)::iterator).orElseThrow(() -> {
            return new IllegalStateException("Unable to calculate BoundingBox without pieces");
        }), random);
    }

    public FacilityKeystone(StructureTemplateManager manager, CompoundTag tag) {
        super(ChangedStructurePieceTypes.FACILITY_KEYSTONE.get(), tag);

        this.header = ActiveFacilityInstance.Header.CODEC.decode(NbtOps.INSTANCE, tag.get("header")).getOrThrow(false, onError -> {}).getFirst();
        this.piecesByZone = PIECES_BY_ZONE_CODEC.decode(NbtOps.INSTANCE, tag.get("piecesByZone")).getOrThrow(true, onError -> {}).getFirst();
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.put("header",
                ActiveFacilityInstance.Header.CODEC.encodeStart(NbtOps.INSTANCE, header).getOrThrow(false, onError -> {})
        );
        tag.put("piecesByZone",
                PIECES_BY_ZONE_CODEC.encodeStart(NbtOps.INSTANCE, piecesByZone).getOrThrow(false, onError -> {})
        );
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager p_226770_, ChunkGenerator p_226771_, RandomSource p_226772_, BoundingBox p_226773_, ChunkPos p_226774_, BlockPos p_226775_) {
        if (level.getLevel() instanceof ChangedGameDataAccessor gameDataAccessor) {
            CompletableFuture.runAsync(() -> {
                String resourceName = this.header.getResourceName();
                if (gameDataAccessor.getChangedGameData().facilities.stream().anyMatch(facility -> facility.getHeader().getResourceName().equals(resourceName)))
                    return; // Already tracked; don't duplicate
                gameDataAccessor.getChangedGameData().trackNewFacility(this.createActiveFacilityInstance());
            }, level.getServer());
        }
    }

    public ActiveFacilityInstance createActiveFacilityInstance() {
        try {
            var zoneInfoBuilder = ImmutableMap.<Zone, ActiveFacilityInstance.ZoneInfo>builder();

            piecesByZone.forEach((zone, boundingBox) -> {
                zoneInfoBuilder.put(zone, new ActiveFacilityInstance.ZoneInfo(
                        FacilityZoneEntities.INSTANCE.getSpawns(zone).stream().map(ActiveFacilityInstance.SpawnInfo::new).toList(),
                        boundingBox.stream().map(pair -> new ActiveFacilityInstance.PieceInfo(pair.getFirst(), pair.getSecond())).toList(),
                        Optional.empty()));
            });

            var facilityInstance = new ActiveFacilityInstance(zoneInfoBuilder.build(), Optional.empty());
            facilityInstance.setHeader(header);
            return facilityInstance;
        } catch (Exception e) {
            Changed.LOGGER.error("Exception while creating ActiveFacilityInstance", e);
            throw e;
        }
    }

    @Override
    public String toString() {
        return "Site " + header.name;
    }
}
