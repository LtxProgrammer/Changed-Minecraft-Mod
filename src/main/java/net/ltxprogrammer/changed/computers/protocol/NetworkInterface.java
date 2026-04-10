package net.ltxprogrammer.changed.computers.protocol;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;

import java.util.UUID;

public interface NetworkInterface {
    record Address(Either<BlockPos, UUID> blockPosOrID) {}

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
        var entity = level.getEntities().get(uuid);
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

    static void sendFrameToAddress(ServerLevel level, Address physicalAddress, CompoundTag dataFrame) {
        var networkInterface = findAtAddress(level, physicalAddress);
        if (networkInterface != null)
            networkInterface.acceptFrame(level, dataFrame);
    }

    void acceptFrame(ServerLevel level, CompoundTag dataFrame);
    void sendFrame(ServerLevel level, CompoundTag dataFrame);
}
