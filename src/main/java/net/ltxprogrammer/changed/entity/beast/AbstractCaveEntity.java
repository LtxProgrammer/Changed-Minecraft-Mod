package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Random;

public abstract class AbstractCaveEntity extends ChangedEntity {
    public AbstractCaveEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
    }

    public static <T extends ChangedEntity> boolean checkEntitySpawnRules(EntityType<T> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random random) {
        if (!isDarkEnoughToSpawn(world, pos, random))
            return false;
        if (pos.getY() > world.getSeaLevel() - 10)
            return false;
        if (random.nextFloat() > 0.5f)
            return false;
        if (!checkSpawnBlock(world, reason, pos))
            return false;
        return Monster.checkAnyLightMonsterSpawnRules(entityType, world, reason, pos, random);
    }
}
