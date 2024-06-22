package net.ltxprogrammer.changed.entity;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public abstract class AttributePresets {
    public static void playerLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(20.0);
    }

    public static void wolfLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.1075);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.95);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }

    public static void catLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.115);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(22.0);
    }

    public static void sharkLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.0875);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.30);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }

    public static void dragonLike(AttributeMap map) {
        map.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
        map.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.85);
        map.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
    }
}
