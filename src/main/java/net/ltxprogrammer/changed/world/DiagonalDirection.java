package net.ltxprogrammer.changed.world;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/// Represents relative block positions that share an edge instead of sharing a face
public enum DiagonalDirection {
    UP_NORTH(Direction.UP, Direction.NORTH),
    UP_EAST(Direction.UP, Direction.EAST),
    UP_SOUTH(Direction.UP, Direction.SOUTH),
    UP_WEST(Direction.UP, Direction.WEST),
    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_EAST(Direction.DOWN, Direction.EAST),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
    DOWN_WEST(Direction.DOWN, Direction.WEST),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    NORTH_WEST(Direction.NORTH, Direction.WEST);

    private final Direction directionA, directionB;
    private final Vec3i normal;
    private static final DiagonalDirection[] VALUES = values();

    DiagonalDirection(Direction directionA, Direction directionB) {
        this.directionA = directionA;
        this.directionB = directionB;
        this.normal = new Vec3i(
                directionA.getStepX() + directionB.getStepX(),
                directionA.getStepY() + directionB.getStepY(),
                directionA.getStepZ() + directionB.getStepZ()
        );
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

    public Stream<BlockPos> intermediatePositions(BlockPos pos) {
        return Stream.of(pos.relative(directionA), pos.relative(directionB));
    }

    public boolean doEitherIntermediateMatch(BlockPos pos, Predicate<BlockPos> blockPosPredicate) {
        return blockPosPredicate.test(pos.relative(directionA)) || blockPosPredicate.test(pos.relative(directionB));
    }

    public boolean doNeitherIntermediateMatch(BlockPos pos, Predicate<BlockPos> blockPosPredicate) {
        return !blockPosPredicate.test(pos.relative(directionA)) && !blockPosPredicate.test(pos.relative(directionB));
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