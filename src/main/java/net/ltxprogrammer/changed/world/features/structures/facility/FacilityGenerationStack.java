package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FacilityGenerationStack {
    private final Stack<FacilityPiece> stack;
    private final BoundingBox parentPieceBoundingBox;
    private final ChunkGenerator chunkGenerator;

    public FacilityGenerationStack(Stack<FacilityPiece> stack, BoundingBox parentPieceBoundingBox, ChunkGenerator chunkGenerator) {
        this.stack = stack;
        this.parentPieceBoundingBox = parentPieceBoundingBox;
        this.chunkGenerator = chunkGenerator;
    }

    public Stream<FacilityPiece> stream() {
        return stack.stream();
    }

    public FacilityPiece getParent() {
        return stack.peek();
    }

    public BoundingBox getParentPieceBoundingBox() {
        return parentPieceBoundingBox;
    }

    public ChunkGenerator getChunkGenerator() {
        return chunkGenerator;
    }

    public int sequentialMatch(Predicate<FacilityPiece> predicate) {
        for (int i = stack.size() - 1; i >= 0; --i) {
            if (!predicate.test(stack.elementAt(i)))
                return stack.size() - (i + 1);
        }

        return stack.size();
    }
}
