package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;

public interface LevelRendererExtension {
    void changed$renderLatexCoverDestruction(PoseStack p_109600_, Camera p_109604_);
    void changed$destroyLatexCoverProgress(int entityId, BlockPos blockPos, int progress);
}
