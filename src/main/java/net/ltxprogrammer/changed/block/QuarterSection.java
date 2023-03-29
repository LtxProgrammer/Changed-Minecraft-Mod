package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public enum QuarterSection implements StringRepresentable {
    BOTTOM_LEFT,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_RIGHT;

    @NotNull
    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isLeft() {
        return this == BOTTOM_LEFT || this == TOP_LEFT;
    }

    public boolean isRight() {
        return this == BOTTOM_RIGHT || this == TOP_RIGHT;
    }

    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
    }

    public boolean isTop() {
        return this == TOP_LEFT || this == TOP_RIGHT;
    }

    @NotNull
    public Collection<QuarterSection> getOtherValues() {
        return switch (this) {
            case BOTTOM_LEFT -> Set.of(TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT);
            case TOP_LEFT -> Set.of(BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT);
            case TOP_RIGHT -> Set.of(BOTTOM_LEFT, TOP_LEFT, BOTTOM_RIGHT);
            case BOTTOM_RIGHT -> Set.of(BOTTOM_LEFT, TOP_LEFT, TOP_RIGHT);
        };
    }

    /**
     * Gets a position relative to the current quarter and position
     * @param current position of the current quarter
     * @param facing perpendicular direction of the quarter
     * @param other quarter to find the position of
     * @return BlockPos relative to the current position
     */
    @NotNull
    public BlockPos getRelative(BlockPos current, Direction facing, QuarterSection other) {
        if (other == this || facing.getAxis() == Direction.Axis.Y)
            return current;

        int x = isLeft() == other.isLeft() ? 0 : (isLeft() ? 1 : -1);
        int y = isBottom() == other.isBottom() ? 0 : (isBottom() ? 1 : -1);

        return switch (facing) {
            case NORTH -> current.offset(-x, y, 0);
            case EAST -> current.offset(0, y, -x);
            case SOUTH -> current.offset(x, y, 0);
            case WEST -> current.offset(0, y, x);
            default -> current;
        };
    }

    public boolean isOnAxis(QuarterSection other, Direction facing, Direction.Axis axis) {
        if (this == other)
            return true;

        return switch (axis) {
            case X -> switch (facing) {
                case NORTH, SOUTH -> this.isBottom() == other.isBottom();
                default -> false;
            };
            case Y -> (this.isBottom() == other.isTop()) && (this.isLeft() == this.isRight());
            case Z -> switch (facing) {
                case EAST, WEST -> this.isBottom() == other.isBottom();
                default -> false;
            };
        };
    }
}
