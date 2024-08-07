package net.ltxprogrammer.changed.block;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.DarkLatexEntity;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LatexCrystal extends TransfurCrystalBlock {
    public final ImmutableList<Supplier<EntityType<? extends DarkLatexEntity>>> spawnable;

    public LatexCrystal(ImmutableList<Supplier<EntityType<? extends DarkLatexEntity>>> spawnable, Supplier<? extends Item> crystal, Properties properties) {
        super(crystal, properties.randomTicks());
        this.spawnable = spawnable;
    }

    private Predicate<ChangedEntity> matchesType(EntityType<?> type) {
        return ChangedEntity -> {
            return ChangedEntity.getType() == type;
        };
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(2000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;

        var entityType = spawnable.get(random.nextInt(spawnable.size())).get();
        if (level.getNearbyEntities(ChangedEntity.class, TargetingConditions.forNonCombat(), null, AABB.of(
                BoundingBox.fromCorners(position.offset(-50, -50, -50), position.offset(50, 50, 50)))).stream()
                .filter(matchesType(entityType)).toList().size() > 35)
            return;
        entityType.spawn(level, null, null, null, position, MobSpawnType.NATURAL, true, true);
        level.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
    }
}
