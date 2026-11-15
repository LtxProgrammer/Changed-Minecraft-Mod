package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.blockentity.LatexContainerRenderer;
import net.ltxprogrammer.changed.client.renderer.blockentity.PillowRenderer;
import net.ltxprogrammer.changed.client.renderer.blockentity.StasisChamberRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ChangedBlockEntityRenderers {
    public static void registerBlockEntityRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ChangedBlockEntities.STASIS_CHAMBER.get(), StasisChamberRenderer::new);
            BlockEntityRenderers.register(ChangedBlockEntities.LATEX_CONTAINER.get(), LatexContainerRenderer::new);
            BlockEntityRenderers.register(ChangedBlockEntities.PILLOW.get(), PillowRenderer::new);
        });
    }
}
