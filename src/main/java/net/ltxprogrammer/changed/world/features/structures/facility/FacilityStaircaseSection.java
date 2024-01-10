package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.world.features.structures.FacilityPieces;
import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

public class FacilityStaircaseSection extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS_MINIMUM = WeightedRandomList.create(
            WeightedEntry.wrap(PieceType.STAIRCASE_SECTION, 1));
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS = WeightedRandomList.create(
            WeightedEntry.wrap(PieceType.STAIRCASE_SECTION, 5),
            WeightedEntry.wrap(PieceType.STAIRCASE_END, 1));
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS_MAXIMUM = WeightedRandomList.create(
            WeightedEntry.wrap(PieceType.STAIRCASE_END, 1));

    public FacilityStaircaseSection(ResourceLocation templateName) {
        super(PieceType.STAIRCASE_SECTION, templateName, LootTables.LOW_TIER_LAB);
    }

    @Override
    public WeightedRandomList<WeightedEntry.Wrapper<PieceType>> getValidNeighbors(FacilityGenerationStack stack) {
        int min = stack.getParentPieceBoundingBox().minY();
        if (min > stack.getChunkGenerator().getSeaLevel() - 10)
            return VALID_NEIGHBORS_MINIMUM; // Force labs to go below sea level
        if (min < stack.getContext().heightAccessor().getMinBuildHeight() + 20)
            return VALID_NEIGHBORS_MAXIMUM; // Force labs to stay above the void

        int sections = stack.sequentialMatch(FacilityPieces.STAIRCASE_SECTIONS::contains);

        if (sections < 3)
            return VALID_NEIGHBORS_MINIMUM;
        if (sections > 7)
            return VALID_NEIGHBORS_MAXIMUM;

        return VALID_NEIGHBORS;
    }
}
