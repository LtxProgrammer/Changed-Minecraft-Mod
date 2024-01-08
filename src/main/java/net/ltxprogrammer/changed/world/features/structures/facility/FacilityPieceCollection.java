package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class FacilityPieceCollection implements Collection<FacilityPiece> {
    private final List<FacilityPiece> pieces;

    public FacilityPieceCollection() {
        this.pieces = new ArrayList<>();
    }

    protected FacilityPieceCollection(List<FacilityPiece> pieces) {
        this.pieces = pieces;
    }

    public FacilityPieceCollection register(FacilityPiece piece) {
        this.pieces.add(piece);
        return this;
    }

    public Optional<FacilityPiece> findNextPiece(Random random) {
        return Util.getRandomSafe(pieces, random);
    }

    public static FacilityPieceCollection of(FacilityPiece... pieces) {
        return new FacilityPieceCollection(List.of(pieces));
    }

    @Override
    public int size() {
        return pieces.size();
    }

    @Override
    public boolean isEmpty() {
        return pieces.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return pieces.contains(o);
    }

    @NotNull
    @Override
    public Iterator<FacilityPiece> iterator() {
        return pieces.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return pieces.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return pieces.toArray(a);
    }

    @Override
    public boolean add(FacilityPiece facilityPiece) {
        return pieces.add(facilityPiece);
    }

    @Override
    public boolean remove(Object o) {
        return pieces.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return pieces.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends FacilityPiece> c) {
        return pieces.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return pieces.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return pieces.retainAll(c);
    }

    @Override
    public void clear() {
        pieces.clear();
    }
}
