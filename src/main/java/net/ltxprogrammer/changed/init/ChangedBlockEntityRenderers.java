package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.blockentity.LatexContainerRenderer;
import net.ltxprogrammer.changed.client.renderer.blockentity.PillowRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedBlockEntityRenderers {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ChangedBlockEntities.LATEX_CONTAINER.get(), LatexContainerRenderer::new);
            BlockEntityRenderers.register(ChangedBlockEntities.PILLOW.get(), PillowRenderer::new);
        });
    }
}
