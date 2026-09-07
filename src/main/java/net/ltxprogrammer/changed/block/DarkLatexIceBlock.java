package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DarkLatexIceBlock extends AbstractLatexIceBlock implements LatexCoveringSource {
    public DarkLatexIceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull LatexCoverState getLatexCoverState(BlockState blockState, BlockPos blockPos) {
        return ChangedLatexTypes.DARK_LATEX.get().sourceCoverState();
    }
}
