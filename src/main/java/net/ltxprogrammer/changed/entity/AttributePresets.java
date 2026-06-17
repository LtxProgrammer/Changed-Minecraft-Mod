package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.init.ChangedAttributes;
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
        map.getInstance(ForgeMod.STEP_HEIGHT_ADDITION.get()).setBaseValue(0.0);
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
        map.getInstance(ChangedAttributes.AIR_CAPACITY.get()).setBaseValue(7.5);
        map.getInstance(ChangedAttributes.JUMP_STRENGTH.get()).setBaseValue(1.25);
        map.getInstance(ChangedAttributes.FALL_RESISTANCE.get()).setBaseValue(2.5);
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

    public static final double MINING_FATIGUE_0 = 0.3;
    public static final double MINING_FATIGUE_1 = 0.09;
    public static final double MINING_FATIGUE_2 = 0.0027;
    public static final double MINING_FATIGUE_3 = 8.1E-4;

    public static final double HASTE_0 = 1.2;
    public static final double HASTE_1 = 1.4;
    public static final double HASTE_2 = 1.6;
    public static final double HASTE_3 = 1.8;
}
