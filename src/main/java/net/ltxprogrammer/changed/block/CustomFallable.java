package net.ltxprogrammer.changed.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Fallable;

public interface CustomFallable extends Fallable {
    ResourceLocation getModelName();
}
