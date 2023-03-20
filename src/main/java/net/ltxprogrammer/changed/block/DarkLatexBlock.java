package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DarkLatexBlock extends AbstractLatexBlock {
    public DarkLatexBlock(Properties p_49795_) {
        super(p_49795_, LatexType.DARK_LATEX, ChangedItems.DARK_LATEX_GOO);
    }

    @Override
    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0 ||
                random.nextInt(20000) > level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE))
            return;

        BlockPos above = position.above();
        if (level.getBlockState(above).is(Blocks.AIR)) {
            level.setBlockAndUpdate(above, ChangedBlocks.LATEX_CRYSTAL.get().defaultBlockState());
        }
    }
}
