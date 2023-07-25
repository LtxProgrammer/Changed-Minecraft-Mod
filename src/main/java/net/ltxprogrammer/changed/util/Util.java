package net.ltxprogrammer.changed.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Util {
    public static Player playerOrNull(Entity entity) {
        if (entity instanceof Player player)
            return player;
        return null;
    }

    public static boolean isRecursive() {
        return isRecursive(300000, 3);
    }

    public static boolean isRecursive(int stackDistance) {
        return isRecursive(stackDistance, 3);
    }

    /**
     * walks the stack back to check for a matching
     * @param stackDistance how far back from the caller to check for recursion. Specify if you know how far back to check.
     * @param callerIndex index in the stack referencing the caller method. Use 1 if calling this method directly
     * @return true - if the call stack contains a recursion to callerIndex. false otherwise.
     */
    public static boolean isRecursive(int stackDistance, int callerIndex) {
        var trace = Thread.currentThread().getStackTrace();
        var caller = trace[callerIndex];
        for (int i = callerIndex + 1; i < stackDistance + callerIndex + 1 && i < trace.length; ++i) {
            var element = trace[i];

            if (element.getClassName().equals(caller.getClassName()) &&
                    element.getMethodName().equals(caller.getMethodName()))
                return true;
        }

        return false;
    }
}
