package net.ltxprogrammer.changed.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Util {
    public static Player playerOrNull(Entity entity) {
        if (entity instanceof Player player)
            return player;
        return null;
    }
}
