package net.ltxprogrammer.changed.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public enum SixSection implements StringRepresentable {
    BOTTOM_LEFT(YAxis.BOTTOM, XAxis.LEFT),
    TOP_LEFT(YAxis.TOP, XAxis.LEFT),
    TOP_MIDDLE(YAxis.TOP, XAxis.MIDDLE),
    TOP_RIGHT(YAxis.TOP, XAxis.RIGHT),
    BOTTOM_RIGHT(YAxis.BOTTOM, XAxis.RIGHT),
    BOTTOM_MIDDLE(YAxis.BOTTOM, XAxis.MIDDLE);

    SixSection(YAxis yAxis, XAxis xAxis) {
        this.yAxis = yAxis;
        this.xAxis = xAxis;
    }

    public enum YAxis {
        BOTTOM, TOP;

        public int relativeTo(YAxis other) {
            return switch (this) {
                case BOTTOM -> switch (other) {
                    case BOTTOM -> 0;
                    case TOP -> 1;
                };
                case TOP -> switch (other) {
                    case BOTTOM -> -1;
                    case TOP -> 0;
                };
            };
        }
    }

    public enum XAxis {
        LEFT, MIDDLE, RIGHT;

        public int relativeTo(XAxis other) {
            return switch (this) {
                case LEFT -> switch (other) {
                    case LEFT -> 0;
                    case MIDDLE -> 1;
                    case RIGHT -> 2;
                };
                case MIDDLE -> switch (other) {
                    case LEFT -> -1;
                    case MIDDLE -> 0;
                    case RIGHT -> 1;
                };
                case RIGHT -> switch (other) {
                    case LEFT -> -2;
                    case MIDDLE -> -1;
                    case RIGHT -> 0;
                };
            };
        }
    }

    public final YAxis yAxis;
    public final XAxis xAxis;

    @NotNull
    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    @NotNull
    public Collection<SixSection> getOtherValues() {
        return Arrays.stream(values()).filter(section -> section != this).collect(Collectors.toSet());
    }

    /**
     * Gets a position relative to the current quarter and position
     * @param current position of the current quarter
     * @param facing perpendicular direction of the quarter
     * @param other quarter to find the position of
     * @return BlockPos relative to the current position
     */
    @NotNull
    public BlockPos getRelative(BlockPos current, Direction facing, SixSection other) {
        if (other == this || facing.getAxis() == Direction.Axis.Y)
            return current;

        int x = this.xAxis.relativeTo(other.xAxis);
        int y = this.yAxis.relativeTo(other.yAxis);

        return switch (facing) {
            case NORTH -> current.offset(-x, y, 0);
            case EAST -> current.offset(0, y, -x);
            case SOUTH -> current.offset(x, y, 0);
            case WEST -> current.offset(0, y, x);
            default -> current;
        };
    }

    public boolean isOnAxis(SixSection other, Direction facing, Direction.Axis axis) {
        if (this == other)
            return true;

        return switch (axis) {
            case X -> switch (facing) {
                case NORTH, SOUTH -> this.yAxis == other.yAxis;
                default -> false;
            };
            case Y -> (this.yAxis != other.yAxis) && (this.xAxis == other.xAxis);
            case Z -> switch (facing) {
                case EAST, WEST -> this.yAxis == other.yAxis;
                default -> false;
            };
        };
    }

    public SixSection getHorizontalNeighbor() {
        return switch (this) {
            case TOP_LEFT -> TOP_RIGHT;
            case TOP_RIGHT -> TOP_LEFT;
            case BOTTOM_LEFT -> BOTTOM_RIGHT;
            case BOTTOM_RIGHT -> BOTTOM_LEFT;
            default -> this;
        };
    }
}
