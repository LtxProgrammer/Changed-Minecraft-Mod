package net.ltxprogrammer.changed.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LevelUtil {
    public static Stream<BlockPos> getBlocksInLine(BlockPos start, BlockPos end, float traceThickness) {
        BlockPos delta = end.subtract(start);
        Vec3 deltaCenter = Vec3.atLowerCornerOf(delta);
        double deltaCheck = 1d / deltaCenter.length();

        final Set<BlockPos> checkedBlocks = new ObjectArraySet<>();
        return Stream.iterate(0d, distance -> distance <= 1d, distance -> distance + deltaCheck)
                .flatMap(checkDistance -> {
                    Vec3 checkCenter = deltaCenter.multiply(checkDistance, checkDistance, checkDistance).add(0.5, 0.5, 0.5);

                    return BlockPos.betweenClosedStream(new AABB(
                            checkCenter.subtract(traceThickness, traceThickness, traceThickness),
                            checkCenter.add(traceThickness, traceThickness, traceThickness)
                    )).map(checkPos -> checkPos.offset(start));
                }).filter(checkedBlocks::add);
    }

    private static @Nullable BlockPos findHighestConnectedBlockImpl(
            BlockPos start,
            @Nullable Direction sourceDir,
            int horizontalDist,
            Predicate<BlockPos> predicate,
            Comparator<BlockPos> tieBreaker) {
        if (!predicate.test(start))
            return null;

        BlockPos highestConnected = findHighestConnectedBlockImpl(start.above(), Direction.DOWN, horizontalDist, predicate, tieBreaker);

        if (horizontalDist <= 0)
            return highestConnected;
        if (highestConnected != null)
            return highestConnected;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (dir == sourceDir)
                continue;

            var horizontalCheck = findHighestConnectedBlockImpl(start.relative(dir), dir.getOpposite(), horizontalDist - 1, predicate, tieBreaker);

            if (horizontalCheck == highestConnected)
                continue;

            if (highestConnected == null) {
                highestConnected = horizontalCheck;
                continue;
            }

            if (horizontalCheck == null)
                continue;

            if (horizontalCheck.getY() > highestConnected.getY() || tieBreaker.compare(horizontalCheck, highestConnected) > 0) {
                highestConnected = horizontalCheck;
            }
        }

        if (highestConnected == null)
            return start;
        else if (highestConnected.getY() > start.getY())
            return highestConnected;
        else
            return tieBreaker.compare(highestConnected, start) > 0 ? highestConnected : start;
    }

    public static @Nullable BlockPos findHighestConnectedFluidBlock(BlockGetter level, BlockPos start, Predicate<FluidState> predicate, int maxDepthCheck) {
        final Set<BlockPos> checkedBlocks = new ObjectArraySet<>();
        final int maxHeight = start.getY() + maxDepthCheck;
        return findHighestConnectedBlockImpl(start, null, 12, checkPos -> {
            if (checkPos.getY() >= maxHeight)
                return false;
            if (!checkedBlocks.add(checkPos))
                return false;
            return predicate.test(level.getFluidState(checkPos));
        }, (left, right) -> {
            return Float.compare(
                    level.getFluidState(left).getOwnHeight(),
                    level.getFluidState(right).getOwnHeight()
            );
        });
    }

    public static double getDepthFromSurfaceOfFluid(BlockGetter level, Vec3 position, Predicate<FluidState> predicate, int maxDepthCheck) {
        BlockPos blockPos = new BlockPos(Mth.floor(position.x), Mth.floor(position.y), Mth.floor(position.z));
        BlockPos surface = findHighestConnectedFluidBlock(level, blockPos, predicate, maxDepthCheck);
        if (surface == null)
            return 0d;

        int blockDepth = surface.getY() - blockPos.getY();
        double surfaceHeight = level.getFluidState(surface).getHeight(level, surface);
        double offsetFromBlock = position.y - Mth.floor(position.y);
        return Math.max(blockDepth + surfaceHeight - offsetFromBlock, 0d);
    }

    public static double getDepthFromSurfaceOfFluid(BlockGetter level, Vec3 position, FluidType fluidType, int maxDepthCheck) {
        return getDepthFromSurfaceOfFluid(level, position, state -> state.getType().getFluidType() == fluidType, maxDepthCheck);
    }

    public static double getDepthFromSurfaceOfWater(BlockGetter level, Vec3 position, int maxDepthCheck) {
        return getDepthFromSurfaceOfFluid(level, position, ForgeMod.WATER_TYPE.get(), maxDepthCheck);
    }

    public static boolean isTouchingShape(BlockPos blockPos, VoxelShape shape, Entity entity) {
        VoxelShape positionedShape = shape.move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
        if (Shapes.joinIsNotEmpty(positionedShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
            return true;

        if (entity.verticalCollision) {
            VoxelShape positionedShapeU = shape.move((double)blockPos.getX(), (double)blockPos.getY() + 0.025, (double)blockPos.getZ());
            if (Shapes.joinIsNotEmpty(positionedShapeU, Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
                return true;
            VoxelShape positionedShapeD = shape.move((double)blockPos.getX(), (double)blockPos.getY() - 0.025, (double)blockPos.getZ());
            if (Shapes.joinIsNotEmpty(positionedShapeD, Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
                return true;
        }

        if (entity.horizontalCollision) {
            VoxelShape positionedShapeN = shape.move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ() + 0.025);
            if (Shapes.joinIsNotEmpty(positionedShapeN, Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
                return true;
            VoxelShape positionedShapeE = shape.move((double)blockPos.getX() + 0.025, (double)blockPos.getY(), (double)blockPos.getZ());
            if (Shapes.joinIsNotEmpty(positionedShapeE, Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
                return true;
            VoxelShape positionedShapeS = shape.move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ() - 0.025);
            if (Shapes.joinIsNotEmpty(positionedShapeS, Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
                return true;
            VoxelShape positionedShapeW = shape.move((double)blockPos.getX() - 0.025, (double)blockPos.getY(), (double)blockPos.getZ());
            if (Shapes.joinIsNotEmpty(positionedShapeW, Shapes.create(entity.getBoundingBox()), BooleanOp.AND))
                return true;
        }

        return false;
    }

    public static boolean isTouchingBlockCollision(BlockGetter level, BlockPos blockPos, BlockState blockState, Entity entity) {
        VoxelShape collisionShape = blockState.getCollisionShape(level, blockPos, CollisionContext.of(entity));
        return isTouchingShape(blockPos, collisionShape, entity);
    }

    public static boolean isTouchingBlockInteraction(BlockGetter level, BlockPos blockPos, BlockState blockState, Entity entity) {
        VoxelShape interactionShape = blockState.getInteractionShape(level, blockPos);
        return isTouchingShape(blockPos, interactionShape, entity);
    }
}
