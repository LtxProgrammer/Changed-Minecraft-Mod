package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface LatexCoveringSource {
    @NotNull
    LatexCoverState getLatexCoverState(BlockState blockState);
}
