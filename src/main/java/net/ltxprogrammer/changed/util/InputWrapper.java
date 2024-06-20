package net.ltxprogrammer.changed.util;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

import java.util.function.Supplier;

public interface InputWrapper {
    float getLeftImpulse();
    float getForwardImpulse();
    boolean getUp();
    boolean getDown();
    boolean getLeft();
    boolean getRight();
    boolean getJumping();
    boolean getShiftKeyDown();

    default Vec2 getMoveVector() {
        return new Vec2(this.getLeftImpulse(), this.getForwardImpulse());
    }

    default boolean hasForwardImpulse() {
        return this.getForwardImpulse() > 1.0E-5F;
    }

    default boolean hasLeftImpulse() {
        return this.getLeftImpulse() > 1.0E-5F;
    }

    class Empty implements InputWrapper {
        @Override
        public float getLeftImpulse() {
            return 0;
        }

        @Override
        public float getForwardImpulse() {
            return 0;
        }

        @Override
        public boolean getUp() {
            return false;
        }

        @Override
        public boolean getDown() {
            return false;
        }

        @Override
        public boolean getLeft() {
            return false;
        }

        @Override
        public boolean getRight() {
            return false;
        }

        @Override
        public boolean getJumping() {
            return false;
        }

        @Override
        public boolean getShiftKeyDown() {
            return false;
        }
    }

    class InputHolder implements Supplier<Input>, InputWrapper {
        private final Input input;

        public InputHolder(Player player) {
            this.input = ((LocalPlayer)player).input;
        }

        public InputHolder(Input input) {
            this.input = input;
        }

        @Override
        public Input get() {
            return input;
        }

        @Override
        public float getLeftImpulse() {
            return input.leftImpulse;
        }

        @Override
        public float getForwardImpulse() {
            return input.forwardImpulse;
        }

        @Override
        public boolean getUp() {
            return input.up;
        }

        @Override
        public boolean getDown() {
            return input.down;
        }

        @Override
        public boolean getLeft() {
            return input.left;
        }

        @Override
        public boolean getRight() {
            return input.right;
        }

        @Override
        public boolean getJumping() {
            return input.jumping;
        }

        @Override
        public boolean getShiftKeyDown() {
            return input.shiftKeyDown;
        }
    }

    static InputWrapper from(Player player) {
        if (UniversalDist.isLocalPlayer(player))
            return new InputHolder(player);
        else
            return new Empty();
    }
}
