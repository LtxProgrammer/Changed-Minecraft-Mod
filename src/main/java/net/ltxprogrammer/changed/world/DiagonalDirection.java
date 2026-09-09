package net.ltxprogrammer.changed.world;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

import java.util.Collection;
import java.util.stream.Stream;

/// Represents relative block positions that share an edge instead of sharing a face
public enum DiagonalDirection {
    UP_NORTH(new Vec3i(0, 1, -1)),
    UP_EAST(new Vec3i(1, 1, 0)),
    UP_SOUTH(new Vec3i(0, 1, 1)),
    UP_WEST(new Vec3i(-1, 1, 0)),
    DOWN_NORTH(new Vec3i(0, -1, -1)),
    DOWN_EAST(new Vec3i(1, -1, 0)),
    DOWN_SOUTH(new Vec3i(0, -1, 1)),
    DOWN_WEST(new Vec3i(-1, -1, 0)),
    NORTH_EAST(new Vec3i(1, 0, -1)),
    SOUTH_EAST(new Vec3i(1, 0, 1)),
    SOUTH_WEST(new Vec3i(-1, 0, 1)),
    NORTH_WEST(new Vec3i(-1, 0, -1));

    private final Vec3i normal;
    private static final DiagonalDirection[] VALUES = values();

    DiagonalDirection(Vec3i normal) {
        this.normal = normal;
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public int getStepZ() {
        return this.normal.getZ();
    }

    public BlockPos relative(BlockPos pos) {
        return pos.offset(normal);
    }

    public BlockPos relative(BlockPos pos, int multiplier) {
        return pos.offset(
                normal.getX() * multiplier,
                normal.getY() * multiplier,
                normal.getZ() * multiplier
        );
    }

    public static DiagonalDirection getRandom(RandomSource random) {
        return Util.getRandom(VALUES, random);
    }

    public static Collection<DiagonalDirection> allShuffled(RandomSource random) {
        return Util.shuffledCopy(values(), random);
    }

    public static Stream<DiagonalDirection> stream() {
        return Stream.of(VALUES);
    }
}