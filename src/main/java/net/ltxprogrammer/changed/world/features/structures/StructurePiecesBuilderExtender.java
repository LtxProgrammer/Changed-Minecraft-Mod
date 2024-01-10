package net.ltxprogrammer.changed.world.features.structures;

import net.minecraft.world.level.levelgen.structure.StructurePiece;

public interface StructurePiecesBuilderExtender {
    boolean removePiece(StructurePiece piece);
    int pieceCount();
}
