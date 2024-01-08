package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.world.features.structures.StructurePiecesBuilderExtender;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(StructurePiecesBuilder.class)
public abstract class StructurePiecesBuilderMixin implements StructurePiecesBuilderExtender, StructurePieceAccessor {
    @Shadow @Final private List<StructurePiece> pieces;

    @Override
    public boolean removePiece(StructurePiece piece) {
        return this.pieces.remove(piece);
    }
}
