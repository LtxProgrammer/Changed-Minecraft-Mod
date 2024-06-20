package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

public class FacilityEntrance extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS = WeightedRandomList.create(WeightedEntry.wrap(PieceType.STAIRCASE_START, 1));

    public FacilityEntrance(ResourceLocation templateName) {
        super(PieceType.ENTRANCE, templateName, LootTables.LOW_TIER_LAB);
    }

    @Override
    public WeightedRandomList<WeightedEntry.Wrapper<PieceType>> getValidNeighbors(FacilityGenerationStack stack) {
        return VALID_NEIGHBORS;
    }
}
