package net.ltxprogrammer.changed.client;

import net.minecraft.client.player.Input;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

public class NullInput extends Input {
    @Override
    public void tick(boolean movingSlowly) {
        super.tick(movingSlowly);
        leftImpulse = 0.0f;
        forwardImpulse = 0.0f;
        up = false;
        down = false;
        left = false;
        right = false;
        jumping = false;
        shiftKeyDown = false;
    }

    @Override
    public @NotNull Vec2 getMoveVector() {
        return Vec2.ZERO;
    }

    @Override
    public boolean hasForwardImpulse() {
        return false;
    }
}
