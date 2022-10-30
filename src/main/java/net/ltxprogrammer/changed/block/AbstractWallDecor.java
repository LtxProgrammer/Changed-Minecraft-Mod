package net.ltxprogrammer.changed.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class AbstractWallDecor extends AbstractCustomShapeBlock {
    public AbstractWallDecor(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
