package net.ltxprogrammer.changed.computers.protocol;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

public interface NetworkInterface {
    record Address(Either<BlockPos, UUID> blockPosOrID) {
        public static Address forBlock(BlockPos blockPos) {
            return new Address(Either.left(blockPos));
        }

        public static Address forEntity(Entity entity) {
            return new Address(Either.right(entity.getUUID()));
        }

        public BlockPos getPosition(ServerLevel level) {
            return blockPosOrID.map(Function.identity(), uuid -> {
                return level.getEntities().get(uuid).blockPosition();
            });
        }
    }

    static NetworkInterface findAtBlockPos(BlockGetter level, BlockPos blockPos) {
        // 1. Block entities having a network interface
        if (level.getBlockEntity(blockPos) instanceof NetworkInterface networkInterface)
            return networkInterface;
        // 2. Fluid states having a network interface (Will probably never happen, but is extensible nonetheless)
        var blockState = level.getBlockState(blockPos);
        if (blockState.getFluidState().getType() instanceof NetworkInterface networkInterface)
            return networkInterface;
        // 3. Block states having a network interface
        if (blockState.getBlock() instanceof NetworkInterface networkInterface)
            return networkInterface;

        return null;
    }

    static NetworkInterface findWithEntityUUID(ServerLevel level, UUID uuid) {
        return findWithEntity(level.getEntities().get(uuid));
    }

    static NetworkInterface findWithEntity(Entity entity) {
        if (entity instanceof LivingEntity livingEntity)
            entity = EntityUtil.maybeGetOverlaying(livingEntity);

        if (entity instanceof NetworkInterface networkInterface)
            return networkInterface;

        return null;
    }

    /// Finds a network compatible device (block/entity) at the given physicalAddress.
    static NetworkInterface findAtAddress(ServerLevel level, Address physicalAddress) {
        return physicalAddress.blockPosOrID.map(blockPos -> findAtBlockPos(level, blockPos), uuid -> findWithEntityUUID(level, uuid));
    }

    static Stream<Address> findNearbyAddresses(ServerLevel level, BlockPos source, int manhattanRadius) {
        AABB boundingBox = AABB.of(BoundingBox.fromCorners(
                source.offset(-manhattanRadius, -manhattanRadius, -manhattanRadius),
                source.offset(manhattanRadius, manhattanRadius, manhattanRadius)
        ));

        var blockAddressStream = BlockPos.betweenClosedStream(boundingBox).filter(blockPos -> {
            return manhattanRadius >= blockPos.distManhattan(source);
        }).filter(blockPos -> Objects.nonNull(findAtBlockPos(level, blockPos))).map(Address::forBlock);

        var entityAddressStream = level.getEntitiesOfClass(Entity.class, boundingBox).stream().filter(entity -> {
            return manhattanRadius >= entity.blockPosition().distManhattan(source);
        }).filter(entity -> Objects.nonNull(findWithEntity(entity))).map(Address::forEntity);

        return Stream.concat(blockAddressStream, entityAddressStream);
    }

    static boolean sendFrameToAddress(ServerLevel level, Address destinationAddress, Address sourceAddress, Frame dataFrame) {
        var networkInterface = findAtAddress(level, destinationAddress);
        if (networkInterface != null) {
            networkInterface.acceptFrame(level, sourceAddress, dataFrame);
            return true;
        }

        return false;
    }

    void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame);
    void sendFrame(ServerLevel level, Frame dataFrame);
}
