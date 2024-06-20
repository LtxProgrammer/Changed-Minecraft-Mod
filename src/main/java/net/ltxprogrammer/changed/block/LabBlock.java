package net.ltxprogrammer.changed.block;

public class LabBlock extends ChangedBlock {
    public LabBlock(Properties properties) {
        super(properties.requiresCorrectToolForDrops());
    }
}
