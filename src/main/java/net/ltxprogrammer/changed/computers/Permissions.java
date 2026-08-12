package net.ltxprogrammer.changed.computers;

public enum Permissions {
    NONE(false, false),
    READ_ONLY(true, false),
    WRITE_ONLY(false, true),
    READ_WRITE(true, true);

    private final boolean read;
    private final boolean write;

    public static Permissions getFor(boolean canRead, boolean canWrite) {
        if (canRead)
            return canWrite ? READ_WRITE : READ_ONLY;
        else
            return canWrite ? WRITE_ONLY : NONE;
    }

    Permissions(boolean read, boolean write) {
        this.read = read;
        this.write = write;
    }

    public boolean canRead() {
        return read;
    }

    public boolean canWrite() {
        return write;
    }

    public Permissions setRead(boolean state) {
        if (state == read)
            return this;
        return getFor(state, write);
    }

    public Permissions setWrite(boolean state) {
        if (state == write)
            return this;
        return getFor(read, state);
    }

    public byte toByte() {
        return (byte) ((read ? 0b100 : 0b0) | (write ? 0b010 : 0b0));
    }

    public static Permissions fromByte(byte b) {
        return getFor((b & 0b100) != 0, (b & 0b010) != 0);
    }
}
