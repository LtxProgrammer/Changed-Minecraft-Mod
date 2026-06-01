package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.world.features.structures.FacilityPieces;
import net.ltxprogrammer.changed.world.features.structures.facility.types.PieceType;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Set;

public abstract class FacilityPiece {
    public final PieceType<?> type;

    protected FacilityPiece(PieceType<?> type) {
        this.type = type;
    }

    public PieceType<?> getType() {
        return type;
    }

    public abstract WeightedRandomList<WeightedPieceNeighborSupplier> getValidNeighbors(FacilityGenerationStack stack);
    public abstract FacilityPieceInstance createStructurePiece(StructureTemplateManager structures, int genDepth);

    public abstract boolean isValidGeneration(FacilityPieces.PlacedFacilityPiece parent, Set<FacilityPieces.PlacedFacilityPiece> directDependents);

    public List<FacilityPieceEvent> getEvents() {
        return List.of();
    }
}
