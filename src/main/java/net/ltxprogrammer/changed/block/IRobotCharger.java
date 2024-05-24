package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.robot.AbstractRobot;
import net.ltxprogrammer.changed.entity.robot.ChargerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public interface IRobotCharger {
    ChargerType getChargerType();

    default void broadcastPosition(Level level, BlockPos pos) {
        AABB aabb = new AABB(pos).inflate(32);
        level.getNearbyEntities(AbstractRobot.class, TargetingConditions.forNonCombat(), null, aabb).forEach(robot ->
                robot.broadcastNearbyCharger(pos, getChargerType()));
    }

    void acceptRobot(BlockState state, Level level, BlockPos pos, AbstractRobot robot);
}
