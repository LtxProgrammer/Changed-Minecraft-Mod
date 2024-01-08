package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

public class FacilityCorridorSection extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS = WeightedRandomList.create(
            WeightedEntry.wrap(PieceType.STAIRCASE_START, 3),
            WeightedEntry.wrap(PieceType.TRANSITION, 3),
            WeightedEntry.wrap(PieceType.CORRIDOR, 15),
            WeightedEntry.wrap(PieceType.ROOM, 5));
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS_MIN = WeightedRandomList.create(
            WeightedEntry.wrap(PieceType.STAIRCASE_START, 1),
            WeightedEntry.wrap(PieceType.TRANSITION, 1),
            WeightedEntry.wrap(PieceType.CORRIDOR, 15),
            WeightedEntry.wrap(PieceType.ROOM, 1));

    public FacilityCorridorSection(ResourceLocation templateName) {
        super(PieceType.CORRIDOR, templateName, LootTables.LOW_TIER_LAB);
    }

    @Override
    public WeightedRandomList<WeightedEntry.Wrapper<PieceType>> getValidNeighbors(FacilityGenerationStack stack) {
        int corridors = stack.sequentialMatch(piece -> piece.type == PieceType.CORRIDOR);
        if (corridors < 5)
            return VALID_NEIGHBORS_MIN;
        return VALID_NEIGHBORS;
    }
}
