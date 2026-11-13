package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;

public class LatexCoverCounter implements PalettedContainer.CountConsumer<LatexCoverState> {
    public int nonEmptyBlockCount;
    public int tickingLatexCoverCount;

    public void accept(LatexCoverState state, int count) {
        if (state.isPresent()) {
            this.nonEmptyBlockCount += count;
            if (state.isRandomlyTicking()) {
                this.tickingLatexCoverCount += count;
            }
        }
    }

    public void countLatexSources(BlockState state, int count) {
        if (state.getBlock() instanceof LatexCoveringSource source) {
            LatexCoverState coverState = source.getLatexCoverState(state);
            if (coverState.isRandomlyTicking())
                this.tickingLatexCoverCount += count;
        }
    }
}
