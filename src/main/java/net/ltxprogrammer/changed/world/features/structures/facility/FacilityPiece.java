package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.List;
import java.util.Set;

public abstract class FacilityPiece {
    public final PieceType type;
    public final Zone zone;

    protected FacilityPiece(PieceType type, Zone zone) {
        this.type = type;
        this.zone = zone;
    }

    public final boolean isValidForZone(Zone zone) {
        return this.zone == zone;
    }

    public abstract WeightedRandomList<WeightedEntry.Wrapper<PieceType>> getValidNeighbors(FacilityGenerationStack stack);
    public abstract FacilityPieceInstance createStructurePiece(StructureManager structures, int genDepth);
}
