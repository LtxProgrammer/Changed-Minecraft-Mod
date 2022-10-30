package net.ltxprogrammer.changed.block;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.entity.beast.DarkLatexEntity;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class LatexCrystal extends AbstractLatexCrystal {
    public LatexCrystal(Properties properties) {
        super(properties.randomTicks());
    }

    private static final List<Supplier<EntityType<? extends DarkLatexEntity>>> DARK_LATEXES = new ImmutableList.Builder<Supplier<EntityType<? extends DarkLatexEntity>>>()
            .add(ChangedEntities.DARK_LATEX_WOLF_MALE::get)
            .add(ChangedEntities.DARK_LATEX_WOLF_FEMALE::get)
            .add(ChangedEntities.DARK_LATEX_DRAGON_MALE::get)
            .add(ChangedEntities.DARK_LATEX_DRAGON_FEMALE::get)
            .add(ChangedEntities.DARK_LATEX_YUFENG::get).build();

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);
        if (random.nextInt(10) == 0) {
            Supplier<EntityType<? extends DarkLatexEntity>> entityTypeSupplier = DARK_LATEXES.get(random.nextInt(DARK_LATEXES.size()));

            entityTypeSupplier.get().spawn(level, null, null, null, position, MobSpawnType.NATURAL, true, true);
            level.setBlock(position, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
