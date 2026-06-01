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

public class FacilityStaircaseEnd extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedPieceNeighborSupplier> VALID_NEIGHBORS = WeightedRandomList.create(
            WeightedPieceNeighborSupplier.of(ChangedFacilityPieceTypes.SPLIT, 1));

    public FacilityStaircaseEnd(ResourceLocation templateName) {
        super(ChangedFacilityPieceTypes.STAIRCASE_END.get(), templateName, Optional.of(LootTables.LOW_TIER_LAB));
    }

    public FacilityStaircaseEnd(ResourceLocation templateName, Optional<ResourceLocation> lootTable) {
        super(ChangedFacilityPieceTypes.STAIRCASE_END.get(), templateName, lootTable);
    }

    public FacilityStaircaseEnd(ResourceLocation templateName, Optional<ResourceLocation> lootTable, List<FacilityPieceEvent> events) {
        super(ChangedFacilityPieceTypes.STAIRCASE_END.get(), templateName, lootTable, events);
    }

    @Override
    public WeightedRandomList<WeightedPieceNeighborSupplier> getValidNeighbors(FacilityGenerationStack stack) {
        return VALID_NEIGHBORS;
    }

    @Override
    public boolean isValidGeneration(FacilityPieces.PlacedFacilityPiece parent, Set<FacilityPieces.PlacedFacilityPiece> directDependents) {
        return !directDependents.isEmpty();
    }
}
