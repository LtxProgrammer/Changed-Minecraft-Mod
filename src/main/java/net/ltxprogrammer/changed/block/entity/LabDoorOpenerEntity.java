package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.block.AbstractLabDoor;
import net.ltxprogrammer.changed.computers.BasicNIC;
import net.ltxprogrammer.changed.computers.protocol.DiscoveryProtocol;
import net.ltxprogrammer.changed.computers.protocol.DoorControlProtocol;
import net.ltxprogrammer.changed.computers.protocol.Frame;
import net.ltxprogrammer.changed.computers.protocol.NetworkInterface;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LabDoorOpenerEntity extends BlockEntity implements NetworkInterface {
    private final OpenableDoor door;
    private final Map<BlockState, AABB> detectionSize = new HashMap<>();
    private final BasicNIC nic;
    private boolean automatic = true;

    protected static final Set<Class<?>> PROTOCOLS = Set.of(DiscoveryProtocol.class, DoorControlProtocol.class);

    public LabDoorOpenerEntity(BlockPos pos, BlockState state, OpenableDoor door) {
        super(ChangedBlockEntities.LAB_DOOR_OPENER.get(), pos, state);
        this.door = door;
        this.nic = new BasicNIC(Address.forBlock(pos.immutable()));
    }

    public static boolean canOpenDoor(@Nullable LivingEntity entity) {
        if (entity == null)
            return false;
        if (entity instanceof Player player)
            return !player.isSpectator();
        if (entity.getType().is(ChangedTags.EntityTypes.CANNOT_OPEN_LAB_DOORS))
            return false;
        if (entity instanceof ChangedEntity)
            return true;
        return entity.getType().is(ChangedTags.EntityTypes.CAN_OPEN_LAB_DOORS);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        if (!automatic)
            return;

        boolean wantedState = !level.getEntitiesOfClass(LivingEntity.class, detectionSize.computeIfAbsent(state, s -> door.getDetectionSize(s, level, pos)), LabDoorOpenerEntity::canOpenDoor).isEmpty();
        if (wantedState != door.isOpen(state, level, pos)) {
            if (wantedState)
                door.openDoor(state, level, pos);
            else
                door.closeDoor(state, level, pos);
        }
    }

    public void handlePacket(ServerLevel level, int logicalSource, Object packet) {
        if (packet instanceof DiscoveryProtocol discoveryProtocol && !discoveryProtocol.isReply()) {
            nic.sendPacket(level, logicalSource, discoveryProtocol.intersect(PROTOCOLS));
        }

        if (packet instanceof DoorControlProtocol doorControlProtocol) {
            doorControlProtocol.automaticState().ifPresent(automaticState -> {
                this.automatic = automaticState;
            });
            doorControlProtocol.openState().ifPresent(wantedState -> {
                var state = this.getBlockState();
                var pos = this.getBlockPos();
                if (wantedState != door.isOpen(state, level, pos)) {
                    if (wantedState)
                        door.openDoor(state, level, pos);
                    else
                        door.closeDoor(state, level, pos);
                }
            });
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LabDoorOpenerEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.nic.tick(serverLevel, pos);
            blockEntity.nic.processPackets(serverLevel, blockEntity::handlePacket);
        }
        if (state.getProperties().contains(BlockStateProperties.POWERED) && !state.getValue(AbstractLabDoor.POWERED))
            return;
        blockEntity.tick(level, pos, state);
    }

    @Override
    public void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame) {
        nic.acceptFrame(level, physicalSource, dataFrame);
    }

    @Override
    public void sendFrame(ServerLevel level, Frame dataFrame) {
        nic.sendFrame(level, dataFrame);
    }
}
