package net.ltxprogrammer.changed.entity;

import net.minecraft.world.InteractionHand;
import net.minecraftforge.common.IExtensibleEnum;
import org.apache.commons.lang3.NotImplementedException;

public enum UseItemMode implements IExtensibleEnum {
    /**
     * The variant can use items like a normal player.
     */
    NORMAL(true, true, true, true, true),
    /**
     * The variant can only hold one item out, in their mouth.
     */
    MOUTH(true, true, false, true, false),
    /**
     * The variant can't hold any items out, nor use them.
     */
    NONE(false, false, false, false, false);

    public final boolean showHotbar;
    public final boolean holdMainHand;
    public final boolean holdOffHand;
    public final boolean interactWithBlocks;
    public final boolean breakBlocks;

    UseItemMode(boolean showHotbar, boolean holdMainHand, boolean holdOffHand, boolean interactWithBlocks, boolean breakBlocks) {
        this.showHotbar = showHotbar;
        this.holdMainHand = holdMainHand;
        this.holdOffHand = holdOffHand;
        this.interactWithBlocks = interactWithBlocks;
        this.breakBlocks = breakBlocks;
    }

    public boolean canUseHand(InteractionHand hand) {
        return switch (hand) {
            case MAIN_HAND -> holdMainHand;
            case OFF_HAND -> holdOffHand;
        };
    }

    public static UseItemMode create(String name, boolean showHotbar, boolean holdMainHand, boolean holdOffHand, boolean interactWithBlocks, boolean breakBlocks) {
        throw new NotImplementedException("Not extended");
    }
}
