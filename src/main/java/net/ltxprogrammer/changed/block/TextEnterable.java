package net.ltxprogrammer.changed.block;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface TextEnterable {
    void setText(String text);
    String getText();
    BlockEntity getSelf();
}
