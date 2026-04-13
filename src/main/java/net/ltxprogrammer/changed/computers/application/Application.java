package net.ltxprogrammer.changed.computers.application;

import net.minecraft.nbt.CompoundTag;

/**
 * Synced state between server and client
 */
public interface Application {
    ApplicationType<?> getType();

    default void acceptPayload(CompoundTag data) {

    }
}
