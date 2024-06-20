package net.ltxprogrammer.changed.util;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityUtil {
    public static Player playerOrNull(Entity entity) {
        if (entity instanceof Player player)
            return player;
        return null;
    }

    public static float getFrictionOnBlock(@NotNull Entity entity) {
        return getFrictionOnBlock(entity.level, new BlockPos(entity.getX(), entity.getBoundingBox().minY - 0.5000001D, entity.getZ()), entity);
    }

    public static float getFrictionOnBlock(LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return getFrictionOnBlock(level.getBlockState(pos), level, pos, entity);
    }

    public static float getFrictionOnBlock(BlockState instance, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        final float originalFriction = instance.getFriction(level, pos, entity);

        if (instance.is(BlockTags.ICE) && entity instanceof LivingEntity livingEntity) {
            return ProcessTransfur.getEntityVariant(livingEntity).map(variant -> {
                if (variant.groundSpeed > 1f) // TODO replace with variant builder property for affected by friction
                    return 0.6f;
                else
                    return originalFriction;
            }).orElse(originalFriction);
        }

        return originalFriction;
    }
}
