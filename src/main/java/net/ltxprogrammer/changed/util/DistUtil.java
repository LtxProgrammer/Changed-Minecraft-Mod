package net.ltxprogrammer.changed.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class DistUtil {
    public static boolean isLocalPlayer(Player player) {
        var returned = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> ClientDistUtil.isLocalPlayer(player));
        return returned != null && returned;
    }
}
