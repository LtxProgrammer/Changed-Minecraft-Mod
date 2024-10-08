package net.ltxprogrammer.changed.entity;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public abstract class AttributePresets {
    /*
    Note for setting attributes
    - Movement speed is automatically adjusted to fit players with a 10:1 ratio
    - Net swim speed is (movement speed * swim speed)
     */

    public static void playerLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(20.0);
    }

    public static void wolfLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.075);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.95);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }

    public static void catLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.15);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(22.0);
    }

    public static void sharkLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.875);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.48);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }

    public static void dragonLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.85);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }
}
