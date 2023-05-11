package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.item.AbstractLatexGoo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DarkLatexBlock extends AbstractLatexBlock {
    public DarkLatexBlock(Properties p_49795_) {
        super(p_49795_, LatexType.DARK_LATEX, ChangedItems.DARK_LATEX_GOO);
    }

    @Override
    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(1000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;

        BlockPos above = position.above();
        if (level.getBlockState(above).is(Blocks.AIR)) {
            level.setBlockAndUpdate(above, ChangedBlocks.LATEX_CRYSTAL.get().defaultBlockState());
        }
    }

    private static final List<Supplier<? extends AbstractLatexCrystal>> SMALL_CRYSTALS = List.of(
            ChangedBlocks.LATEX_CRYSTAL,
            ChangedBlocks.DARK_LATEX_DRAGON_CRYSTAL,
            ChangedBlocks.LATEX_BEIFENG_CRYSTAL_SMALL,
            ChangedBlocks.LATEX_WOLF_CRYSTAL_SMALL
    );

    private static final List<Supplier<? extends AbstractLatexCrystal>> CRYSTALS = List.of(
            ChangedBlocks.DARK_LATEX_CRYSTAL_LARGE,
            ChangedBlocks.LATEX_BEIFENG_CRYSTAL,
            ChangedBlocks.LATEX_WOLF_CRYSTAL
    );

    @SubscribeEvent
    public static void onLatexCover(AbstractLatexGoo.CoveringBlockEvent event) {
        if (event.latexType != LatexType.DARK_LATEX)
            return;

        if (event.originalState.is(Blocks.GRASS) || event.originalState.is(BlockTags.SMALL_FLOWERS) || event.originalState.is(Blocks.FERN) || event.originalState.is(BlockTags.SAPLINGS)) {
            event.setPlannedState(SMALL_CRYSTALS.get(event.level.random.nextInt(SMALL_CRYSTALS.size())).get().defaultBlockState());
            return;
        }

        if (event.originalState.is(Blocks.TALL_GRASS) || event.originalState.is(Blocks.LARGE_FERN) || event.originalState.is(BlockTags.TALL_FLOWERS)) {
            var crystal = CRYSTALS.get(event.level.random.nextInt(CRYSTALS.size()));
            switch (event.originalState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                case UPPER -> {
                    event.level.setBlockAndUpdate(event.blockPos.below(), crystal.get().defaultBlockState()
                            .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
                    event.setPlannedState(crystal.get().defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                }

                case LOWER -> {
                    event.level.setBlockAndUpdate(event.blockPos.above(), crystal.get().defaultBlockState()
                            .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                    event.setPlannedState(crystal.get().defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
                }
            }
        }
    }
}
