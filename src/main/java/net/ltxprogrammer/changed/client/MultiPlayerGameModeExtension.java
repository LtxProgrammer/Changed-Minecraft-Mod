package net.ltxprogrammer.changed.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface MultiPlayerGameModeExtension {
    boolean changed$destroyLatexCover(BlockPos pos);
    boolean changed$startDestroyLatexCover(BlockPos pos, Direction direction);
    void changed$stopDestroyLatexCover();
    boolean changed$continueDestroyLatexCover(BlockPos pos, Direction direction);

    boolean changed$isDestroyingLatexCover();
}
