package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.util.InputWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;

public abstract class PlayerMoverInstance<T extends PlayerMover<?>> {
    public final T parent;

    public PlayerMoverInstance(T parent) {
        this.parent = parent;
    }

    public boolean is(PlayerMover<?> mover) {
        return mover == parent;
    }

    public void saveTo(CompoundTag tag) {}
    public void readFrom(CompoundTag tag) {}

    public abstract void aiStep(Player player, InputWrapper input, LogicalSide side);
    public abstract void serverAiStep(Player player, InputWrapper input, LogicalSide side);

    /**
     * Test if the player mover should remove itself
     * @param player player
     * @param input wrapped input
     * @param side logical side of the function
     * @return true - if the mover should be removed.
     */
    public abstract boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side);
    public EntityDimensions getDimensions(Pose pose, EntityDimensions currentDimensions) {
        return currentDimensions;
    }
    public float getEyeHeight(Pose pose, float eyeHeight) {
        return eyeHeight;
    }
}
