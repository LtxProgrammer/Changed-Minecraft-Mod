package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.block.AbstractLabDoor;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;

public class LabDoorOpenerEntity extends BlockEntity {
    private final OpenableDoor door;
    private final Map<BlockState, AABB> detectionSize = new HashMap<>();

    public LabDoorOpenerEntity(BlockPos pos, BlockState state, OpenableDoor door) {
        super(ChangedBlockEntities.LAB_DOOR_OPENER.get(), pos, state);
        this.door = door;
    }

    private boolean canOpenDoor(LivingEntity entity) {
        if (entity instanceof Player)
            return true;
        if (entity.getType().is(ChangedTags.EntityTypes.CANNOT_OPEN_LAB_DOORS))
            return false;
        if (entity instanceof LatexEntity)
            return true;
        return entity.getType().is(ChangedTags.EntityTypes.CAN_OPEN_LAB_DOORS);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        boolean wantedState = !level.getEntitiesOfClass(LivingEntity.class, detectionSize.computeIfAbsent(state, s -> door.getDetectionSize(s, level, pos)), this::canOpenDoor).isEmpty();
        if (wantedState != door.isOpen(state, level, pos)) {
            if (wantedState)
                door.openDoor(state, level, pos);
            else
                door.closeDoor(state, level, pos);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LabDoorOpenerEntity blockEntity) {
        if (state.getProperties().contains(BlockStateProperties.POWERED) && !state.getValue(AbstractLabDoor.POWERED))
            return;
        blockEntity.tick(level, pos, state);
    }
}
