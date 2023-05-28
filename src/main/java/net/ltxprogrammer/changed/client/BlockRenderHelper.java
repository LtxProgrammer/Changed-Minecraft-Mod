package net.ltxprogrammer.changed.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRenderHelper {
    public static boolean canBlockRenderAsSolid(BlockState blockState) {
        return ItemBlockRenderTypes.canRenderInLayer(blockState, RenderType.solid());
    }
}
