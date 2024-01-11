package net.ltxprogrammer.changed.world.features.structures.facility;

public enum PieceType {
    ENTRANCE(true),
    STAIRCASE_START(true),
    STAIRCASE_SECTION(false),
    STAIRCASE_END(false),
    CORRIDOR(true),
    SPLIT(true),
    TRANSITION(true),
    SEAL(true),
    ROOM(true);

    private final boolean consumesSpan;

    PieceType(boolean consumesSpan) {
        this.consumesSpan = consumesSpan;
    }

    public boolean shouldConsumeSpan() {
        return this.consumesSpan;
    }
}
