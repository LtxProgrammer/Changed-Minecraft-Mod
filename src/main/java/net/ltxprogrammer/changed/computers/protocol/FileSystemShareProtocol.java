package net.ltxprogrammer.changed.computers.protocol;

import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.computers.Permissions;

public final class FileSystemShareProtocol extends Packet {
    private final Folder rootFolder;
    private final Permissions permissions;

    public FileSystemShareProtocol(Folder rootFolder, Permissions permissions) {
        this.rootFolder = rootFolder;
        this.permissions = permissions;
    }

    public Folder getRootFolder() {
        return rootFolder;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public static final class Query extends Packet {
        public static final Query INSTANCE = new Query();

        private Query() {}
    }
}
