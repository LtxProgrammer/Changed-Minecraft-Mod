package net.ltxprogrammer.changed.world.features.structures.facility;

import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FacilityGenerationStack {
    private final Stack<FacilityPiece> stack;

    public FacilityGenerationStack(Stack<FacilityPiece> stack) {
        this.stack = stack;
    }

    public Stream<FacilityPiece> stream() {
        return stack.stream();
    }

    public FacilityPiece getParent() {
        return stack.peek();
    }

    public int sequentialMatch(Predicate<FacilityPiece> predicate) {
        for (int i = stack.size() - 1; i >= 0; --i) {
            if (!predicate.test(stack.elementAt(i)))
                return stack.size() - (i + 1);
        }

        return stack.size();
    }
}
