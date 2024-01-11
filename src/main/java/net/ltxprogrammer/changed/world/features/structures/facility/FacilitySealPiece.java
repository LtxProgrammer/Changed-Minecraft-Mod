package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.world.features.structures.LootTables;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

public class FacilitySealPiece extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS = WeightedRandomList.create();

    public FacilitySealPiece(ResourceLocation templateName) {
        super(PieceType.SEAL, templateName, LootTables.LOW_TIER_LAB);
    }

    @Override
    public WeightedRandomList<WeightedEntry.Wrapper<PieceType>> getValidNeighbors(FacilityGenerationStack stack) {
        return VALID_NEIGHBORS;
    }
}
