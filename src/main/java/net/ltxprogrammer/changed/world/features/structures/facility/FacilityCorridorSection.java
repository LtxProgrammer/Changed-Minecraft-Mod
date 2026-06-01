package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.init.ChangedFacilityPieceTypes;
import net.ltxprogrammer.changed.world.features.structures.FacilityPieces;
import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.ltxprogrammer.changed.world.features.structures.facility.types.PieceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class  FacilityCorridorSection extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 3),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.SPLIT, 12),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 8),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.ROOM, 5));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_MIN = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 1),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.SPLIT, 15),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 15),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.ROOM, 5));

    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_LOW = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.SPLIT, 12),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 8),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.ROOM, 5));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_MIN_LOW = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.SPLIT, 15),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 15),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.ROOM, 5));

    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_HIGH_SPAN = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 3),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.SPLIT, 12),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 8));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_MIN_HIGH_SPAN = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 1),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.SPLIT, 15),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 15));

    public FacilityCorridorSection(ResourceLocation templateName) {
        super(ChangedFacilityPieceTypes.CORRIDOR.get(), templateName, Optional.of(LootTables.LOW_TIER_LAB));
    }

    public FacilityCorridorSection(ResourceLocation templateName, Optional<ResourceLocation> lootTable) {
        super(ChangedFacilityPieceTypes.CORRIDOR.get(), templateName, lootTable);
    }

    public FacilityCorridorSection(ResourceLocation templateName, Optional<ResourceLocation> lootTable, List<FacilityPieceEvent> events) {
        super(ChangedFacilityPieceTypes.CORRIDOR.get(), templateName, lootTable, events);
    }

    @Override
    public WeightedRandomList<WeightedPieceNeighborSupplier> getValidNeighbors(FacilityGenerationStack stack) {
        int corridors = stack.sequentialMatch(piece -> piece.getFacilityPiece().type == ChangedFacilityPieceTypes.CORRIDOR.get());

        int min = stack.getParentPieceBoundingBox().minY();
        if (min < stack.getContext().heightAccessor().getMinBuildHeight() + 60) {
            if (corridors < 2)
                return VALID_NEIGHBORS_MIN_LOW;
            return VALID_NEIGHBORS_LOW;
        }

        if (stack.getDepthRemaining() > 4) {
            if (corridors < 2)
                return VALID_NEIGHBORS_MIN_HIGH_SPAN;
            return VALID_NEIGHBORS_HIGH_SPAN;
        }

        if (corridors < 2)
            return VALID_NEIGHBORS_MIN;
        return VALID_NEIGHBORS;
    }

    @Override
    public boolean isValidGeneration(FacilityPieces.PlacedFacilityPiece parent, Set<FacilityPieces.PlacedFacilityPiece> directDependents) {
        return !directDependents.isEmpty();
    }
}
