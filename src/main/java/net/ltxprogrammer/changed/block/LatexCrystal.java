package net.ltxprogrammer.changed.block;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.entity.beast.DarkLatexEntity;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class LatexCrystal extends AbstractLatexCrystal {
    ImmutableList<Supplier<EntityType<? extends DarkLatexEntity>>> spawnable;

    public LatexCrystal(ImmutableList<Supplier<EntityType<? extends DarkLatexEntity>>> spawnable, Supplier<Item> crystal, Properties properties) {
        super(crystal, properties.randomTicks());
        this.spawnable = spawnable;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);
        if (random.nextInt(10) == 0) {
            Supplier<EntityType<? extends DarkLatexEntity>> entityTypeSupplier = spawnable.get(random.nextInt(spawnable.size()));

            entityTypeSupplier.get().spawn(level, null, null, null, position, MobSpawnType.NATURAL, true, true);
            level.setBlock(position, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
