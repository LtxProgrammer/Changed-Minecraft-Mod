package net.ltxprogrammer.changed.util;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityUtil {
    public static Player playerOrNull(Entity entity) {
        if (entity instanceof Player player)
            return player;
        return null;
    }

    /**
     * Checks if the entity is a transfurred player's form, and returns the underlying entity if so. Otherwise, returns the entity.
     * @param entity Entity to check
     * @return Underlying entity or original entity
     */
    public static LivingEntity maybeGetUnderlying(LivingEntity entity) {
        if (entity instanceof ChangedEntity changedEntity)
            return changedEntity.maybeGetUnderlying();
        return entity;
    }

    /**
     * Checks if the entity is a transfurred player, and returns the changed entity if so. Otherwise, returns the entity.
     * @param entity Entity to check
     * @return Changed entity or original entity
     */
    public static LivingEntity maybeGetOverlaying(LivingEntity entity) {
        return ProcessTransfur.getPlayerTransfurVariantSafe(playerOrNull(entity))
                .map(instance -> (LivingEntity)instance.getChangedEntity())
                .orElse(entity);
    }

    public static float getFrictionOnBlock(BlockState instance, float originalFriction, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        if (instance.is(BlockTags.ICE) && entity instanceof LivingEntity livingEntity) {
            return ProcessTransfur.getEntityVariant(livingEntity).map(variant -> {
                if (livingEntity.getAttributeValue(Attributes.MOVEMENT_SPEED) > 0.1f) // TODO replace with variant builder property for affected by friction
                    return 0.6f;
                else
                    return originalFriction;
            }).orElse(originalFriction);
        }

        return originalFriction;
    }

    public static BlockPos getEyeBlock(Entity entity) {
        final var eyePos = entity.getEyePosition();
        return new BlockPos(Mth.floor(eyePos.x), Mth.floor(eyePos.y), Mth.floor(eyePos.z));
    }

    public static BlockPos getBlock(Vec3 pos) {
        return new BlockPos(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));
    }

    public static BlockPos getBlock(double x, double y, double z) {
        return new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }

    public static void setNoControlTicks(LivingEntity entity, int ticks) {
        if (entity instanceof LivingEntityDataExtension ext)
            ext.setNoControlTicks(ticks);
    }

    public static int getNoControlTicks(LivingEntity entity) {
        if (entity instanceof LivingEntityDataExtension ext)
            return ext.getNoControlTicks();
        return 0;
    }

    public static void setInvertControlTicks(LivingEntity entity, int ticks) {
        if (entity instanceof LivingEntityDataExtension ext)
            ext.setInvertControlTicks(ticks);
    }

    public static int getInvertControlTicks(LivingEntity entity) {
        if (entity instanceof LivingEntityDataExtension ext)
            return ext.getInvertControlTicks();
        return 0;
    }

    public static BoundingBox roundAABB(AABB box) {
        return new BoundingBox(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ), Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ));
    }
}
