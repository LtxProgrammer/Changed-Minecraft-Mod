package net.ltxprogrammer.changed.computers.protocol;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface NetworkInterface {
    int MAX_LOCAL_TRAVEL_DISTANCE = 32;
    Address BROADCAST = null;

    record Address(Either<BlockPos, UUID> blockPosOrID) {
        public static Address forBlock(BlockPos blockPos) {
            return new Address(Either.left(blockPos.immutable()));
        }

        public static Address forEntity(Entity entity) {
            return new Address(Either.right(entity.getUUID()));
        }

        public @Nullable BlockPos getPosition(ServerLevel level) {
            return blockPosOrID.map(Function.identity(), uuid -> {
                var entity = level.getEntities().get(uuid);
                return entity == null ? null : entity.blockPosition();
            });
        }

        public boolean isRoutable(ServerLevel level) {
            return blockPosOrID.map(pos -> true, uuid -> level.getEntities().get(uuid) != null);
        }

        public int getManhattanDistance(ServerLevel level, Address other) {
            BlockPos myPos = getPosition(level);
            BlockPos otherPos = other.getPosition(level);

            if (myPos == null || otherPos == null)
                return Integer.MAX_VALUE;
            return myPos.distManhattan(otherPos);
        }

        public static Address decode(Tag tag) {
            if (tag instanceof CompoundTag compoundTag)
                return Address.forBlock(NbtUtils.readBlockPos(compoundTag));
            else
                return new Address(Either.right(NbtUtils.loadUUID(tag)));
        }

        public Tag encode() {
            return blockPosOrID.map(
                    NbtUtils::writeBlockPos,
                    NbtUtils::createUUID
            );
        }

        @Override
        public String toString() {
            return blockPosOrID.map(
                    pos -> {
                        int lsx = (Mth.abs(pos.getX()) & 0x3FFFFFF) * (pos.getX() >= 0 ? 1 : -1) + 0x2000000; // 27 bits
                        int lsy = (Mth.abs(pos.getY()) & 0x00001FF) * (pos.getY() >= 0 ? 1 : -1) + 0x0000100; // 10 bits
                        int lsz = (Mth.abs(pos.getZ()) & 0x3FFFFFF) * (pos.getZ() >= 0 ? 1 : -1) + 0x2000000; // 27 bits

                        String xc = Integer.toString(lsx, 16).toUpperCase(Locale.ROOT);
                        String yc = Integer.toString(lsy, 16).toUpperCase(Locale.ROOT);
                        String zc = Integer.toString(lsz, 16).toUpperCase(Locale.ROOT);

                        return String.join(":",
                                ("0".repeat(7 - xc.length())) + xc,
                                ("0".repeat(3 - yc.length())) + yc,
                                ("0".repeat(7 - zc.length())) + zc);
                    },
                    uuid -> {
                        return uuid.toString(); // Wip
                    }
            );
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

    static boolean broadcastFrame(ServerLevel level, Address origin, Address immediateSource, @Nullable Address previousSource, Frame dataFrame, int ttl, Collection<Address> nearbyAddresses) {
        AtomicBoolean anyHit = new AtomicBoolean(false);

        Predicate<Address> rebroadcastPredicate;
        if (previousSource == null)
            rebroadcastPredicate = address -> true;
        else
            rebroadcastPredicate = address -> address.getManhattanDistance(level, previousSource) > MAX_LOCAL_TRAVEL_DISTANCE;

        nearbyAddresses.stream()
                .filter(address -> !address.equals(immediateSource)) // Don't broadcast back to immediate origin
                .filter(rebroadcastPredicate) // Don't rebroadcast to devices previous device would hit
                .forEach(nearbyAddress -> {
            var networkInterface = findAtAddress(level, nearbyAddress);
            if (networkInterface != null) {
                networkInterface.acceptFrame(level, origin, dataFrame);
                anyHit.set(true);
            }

            if (networkInterface instanceof Router router) {
                router.handoffBroadcastFrame(level, origin, immediateSource, dataFrame, ttl);
            }
        });

        return anyHit.getAcquire();
    }

    static boolean broadcastFrame(ServerLevel level, Address origin, Frame dataFrame, int ttl, Collection<Address> nearbyAddresses) {
        return broadcastFrame(level, origin, origin, null, dataFrame, ttl, nearbyAddresses);
    }

    static boolean sendFrameToAddress(ServerLevel level, @Nullable Address destinationAddress, Address origin, Address immediateSource, @Nullable Address previousSource, Frame dataFrame, int ttl, Collection<Address> nearbyAddresses) {
        if (destinationAddress == null)
            return broadcastFrame(level, origin, immediateSource, previousSource, dataFrame, ttl, nearbyAddresses);

        if (destinationAddress.equals(origin)) { // Loopback
            var networkInterface = findAtAddress(level, destinationAddress);
            if (networkInterface != null) {
                networkInterface.acceptFrame(level, origin, dataFrame);
                return true;
            } else return false;
        }

        if (destinationAddress.getManhattanDistance(level, immediateSource) <= MAX_LOCAL_TRAVEL_DISTANCE) {
            var networkInterface = findAtAddress(level, destinationAddress);
            if (networkInterface != null) {
                nearbyAddresses.add(destinationAddress); // Cache nearby address
                networkInterface.acceptFrame(level, origin, dataFrame);
                return true;
            } else return false;
        } else {
            nearbyAddresses.remove(destinationAddress); // Remove in case the address was previously cached

            var closestToDestination = nearbyAddresses.stream().filter(address -> !address.equals(immediateSource))
                    .min(Comparator.comparingInt(address -> address.getManhattanDistance(level, destinationAddress)));
            if (closestToDestination.isEmpty())
                return false;

            var networkInterface = findAtAddress(level, closestToDestination.get());
            if (networkInterface instanceof Router router) {
                router.handoffUnicastFrame(level, origin, immediateSource, destinationAddress, dataFrame, ttl);
                return true;
            }

            return false;
        }
    }

    static boolean sendFrameToAddress(ServerLevel level, @Nullable Address destinationAddress, Address origin, Frame dataFrame, int ttl, Collection<Address> nearbyAddresses) {
        return sendFrameToAddress(level, destinationAddress, origin, origin, null, dataFrame, ttl, nearbyAddresses);
    }

    void acceptNeighbor(ServerLevel level, Address physicalSource);

    void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame);
    void sendOutFrame(ServerLevel level, @Nullable Address destination, Frame frame, int ttl);

    default void sendOutFrame(ServerLevel level, @Nullable Address destination, Frame frame) {
        this.sendOutFrame(level, destination, frame, 4);
    }
}
