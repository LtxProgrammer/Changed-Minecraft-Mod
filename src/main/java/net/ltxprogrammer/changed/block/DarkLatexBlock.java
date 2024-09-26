package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.AbstractLatexItem;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
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

    private static final List<Supplier<? extends TransfurCrystalBlock>> SMALL_CRYSTALS = List.of(
            ChangedBlocks.LATEX_CRYSTAL,
            ChangedBlocks.DARK_DRAGON_CRYSTAL,
            ChangedBlocks.BEIFENG_CRYSTAL_SMALL,
            ChangedBlocks.WOLF_CRYSTAL_SMALL
    );

    private static final List<Supplier<? extends TransfurCrystalBlock>> CRYSTALS = List.of(
            ChangedBlocks.DARK_LATEX_CRYSTAL_LARGE,
            ChangedBlocks.BEIFENG_CRYSTAL,
            ChangedBlocks.WOLF_CRYSTAL
    );

    @Override
    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(5000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;

        BlockPos above = position.above();
        BlockPos above2 = above.above();
        boolean isAboveAir = level.getBlockState(above).is(Blocks.AIR);
        boolean isAbove2Air = level.getBlockState(above2).is(Blocks.AIR);
        if (isAboveAir && canSupportRigidBlock(level, position)) { // Do growth event
            long crystalCount = level.getBlockStates(new AABB(position).inflate(3.0))
                    .filter(neighbor -> neighbor.is(ChangedTags.Blocks.LATEX_CRYSTAL))
                    .count();

            if (crystalCount > 6) return;

            if (random.nextFloat() < 0.75f || !isAbove2Air) {
                level.setBlockAndUpdate(above, Util.getRandom(SMALL_CRYSTALS, random).get().defaultBlockState());
            } else {
                final var newBlockState = Util.getRandom(CRYSTALS, random).get().defaultBlockState();
                level.setBlockAndUpdate(above, newBlockState.setValue(AbstractDoubleTransfurCrystal.HALF, DoubleBlockHalf.LOWER));
                level.setBlockAndUpdate(above2, newBlockState.setValue(AbstractDoubleTransfurCrystal.HALF, DoubleBlockHalf.UPPER));
            }
        }
    }

    @SubscribeEvent
    public static void onLatexCover(AbstractLatexItem.CoveringBlockEvent event) {
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
