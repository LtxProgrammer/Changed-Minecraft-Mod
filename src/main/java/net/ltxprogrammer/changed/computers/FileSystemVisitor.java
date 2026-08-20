package net.ltxprogrammer.changed.computers;

public interface FileSystemVisitor {
    void visit(char driveLetter, DiscData discData, boolean canEject);
}
