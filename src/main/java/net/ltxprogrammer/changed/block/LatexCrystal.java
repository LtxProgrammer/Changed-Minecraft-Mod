package net.ltxprogrammer.changed.block;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.entity.LatexEntity;
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
import java.util.function.Supplier;

public class LatexCrystal extends AbstractLatexCrystal {
    public final ImmutableList<Supplier<EntityType<? extends DarkLatexEntity>>> spawnable;

    public LatexCrystal(ImmutableList<Supplier<EntityType<? extends DarkLatexEntity>>> spawnable, Supplier<? extends Item> crystal, Properties properties) {
        super(crystal, properties.randomTicks());
        this.spawnable = spawnable;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(2000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;

        if (level.getNearbyEntities(LatexEntity.class, TargetingConditions.forNonCombat(), null, AABB.of(
                BoundingBox.fromCorners(position.offset(-10, -10, -10), position.offset(10, 10, 10)))).size() > 2)
            return;
        spawnable.get(random.nextInt(spawnable.size())).get().spawn(level, null, null, null, position, MobSpawnType.NATURAL, true, true);
        level.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
    }
}
