package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.init.ChangedFacilityPieceTypes;
import net.ltxprogrammer.changed.world.features.structures.FacilityPieces;
import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.ltxprogrammer.changed.world.features.structures.facility.types.PieceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FacilityStaircaseSection extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_MINIMUM = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_SECTION, 1));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_HIGH = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_SECTION, 5),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_END, 1));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_LOW = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_SECTION, 2),
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_END, 5));
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS_MAXIMUM = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.STAIRCASE_END, 1));

    public FacilityStaircaseSection(ResourceLocation templateName) {
        super(ChangedFacilityPieceTypes.STAIRCASE_SECTION.get(), templateName, Optional.of(LootTables.LOW_TIER_LAB));
    }

    public FacilityStaircaseSection(ResourceLocation templateName, Optional<ResourceLocation> lootTable) {
        super(ChangedFacilityPieceTypes.STAIRCASE_SECTION.get(), templateName, lootTable);
    }

    public FacilityStaircaseSection(ResourceLocation templateName, Optional<ResourceLocation> lootTable, List<FacilityPieceEvent> events) {
        super(ChangedFacilityPieceTypes.STAIRCASE_SECTION.get(), templateName, lootTable, events);
    }

    @Override
    public WeightedRandomList<WeightedPieceNeighborSupplier> getValidNeighbors(FacilityGenerationStack stack) {
        int min = stack.getParentPieceBoundingBox().minY();
        if (min > stack.getChunkGenerator().getSeaLevel() - 20)
            return VALID_NEIGHBORS_MINIMUM; // Force labs to go below sea level
        if (min < stack.getContext().heightAccessor().getMinBuildHeight() + 20)
            return VALID_NEIGHBORS_MAXIMUM; // Force labs to stay above the void

        final var staircaseSections = FacilityPieces.getPiecesOfType(ChangedFacilityPieceTypes.STAIRCASE_SECTION.get());
        int sections = stack.sequentialMatch(staircaseSections::contains);

        if (min < stack.getContext().heightAccessor().getMinBuildHeight() + 60) {
            if (sections <= 0)
                return VALID_NEIGHBORS_MINIMUM;
            if (sections > 3)
                return VALID_NEIGHBORS_MAXIMUM;
            return VALID_NEIGHBORS_LOW;
        }

        if (sections < 3)
            return VALID_NEIGHBORS_MINIMUM;
        if (sections > 7)
            return VALID_NEIGHBORS_MAXIMUM;

        return VALID_NEIGHBORS_HIGH;
    }

    @Override
    public boolean isValidGeneration(FacilityPieces.PlacedFacilityPiece parent, Set<FacilityPieces.PlacedFacilityPiece> directDependents) {
        return !directDependents.isEmpty();
    }
}
