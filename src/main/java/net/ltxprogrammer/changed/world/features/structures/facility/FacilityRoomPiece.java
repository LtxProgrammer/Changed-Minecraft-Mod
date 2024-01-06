package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

public class FacilityRoomPiece extends FacilitySinglePiece {
    private static final WeightedRandomList<WeightedEntry.Wrapper<PieceType>> VALID_NEIGHBORS = WeightedRandomList.create(
            WeightedEntry.wrap(PieceType.ROOM, 1));

    public FacilityRoomPiece(ResourceLocation templateName, ResourceLocation lootTable) {
        super(PieceType.ROOM, templateName, lootTable);
    }

    @Override
    public WeightedRandomList<WeightedEntry.Wrapper<PieceType>> getValidNeighbors(FacilityGenerationStack stack) {
        return VALID_NEIGHBORS;
    }
}
