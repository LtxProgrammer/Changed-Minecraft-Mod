package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.init.ChangedFacilityPieceTypes;
import net.ltxprogrammer.changed.world.features.structures.FacilityPieces;
import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FacilitySplitSection extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 3),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 8),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.ROOM, 5));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_MIN = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 1),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 15),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.ROOM, 1));

    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_HIGH_SPAN = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 3),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 8));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_MIN_HIGH_SPAN = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_START, 1),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.CORRIDOR, 15));

    public final int expectedDependents;

    public FacilitySplitSection(ResourceLocation templateName) {
        this(templateName, Optional.of(LootTables.LOW_TIER_LAB));
    }

    public FacilitySplitSection(ResourceLocation templateName, Optional<ResourceLocation> lootTable) {
        this(templateName, 2, lootTable);
    }

    public FacilitySplitSection(ResourceLocation templateName, int expectedDependents, Optional<ResourceLocation> lootTable) {
        this(templateName, expectedDependents, lootTable, List.of());
    }

    public FacilitySplitSection(ResourceLocation templateName, int expectedDependents, Optional<ResourceLocation> lootTable, List<FacilityPieceEvent> events) {
        super(ChangedFacilityPieceTypes.SPLIT.get(), templateName, lootTable, events);
        this.expectedDependents = expectedDependents;
    }

    @Override
    public WeightedRandomList<WeightedPieceNeighborSupplier> getValidNeighbors(FacilityGenerationStack stack) {
        int corridors = stack.sequentialMatch(piece -> piece.getFacilityPiece().type == ChangedFacilityPieceTypes.CORRIDOR.get());
        if (stack.getDepthRemaining() > 4) {
            if (corridors < 5)
                return VALID_NEIGHBORS_MIN_HIGH_SPAN;
            return VALID_NEIGHBORS_HIGH_SPAN;
        }

        if (corridors < 5)
            return VALID_NEIGHBORS_MIN;
        return VALID_NEIGHBORS;
    }

    @Override
    public boolean isValidGeneration(FacilityPieces.PlacedFacilityPiece parent, Set<FacilityPieces.PlacedFacilityPiece> directDependents) {
        return directDependents.size() >= expectedDependents;
    }
}
