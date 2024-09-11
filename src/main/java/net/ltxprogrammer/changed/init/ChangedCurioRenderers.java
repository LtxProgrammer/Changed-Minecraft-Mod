package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.curio.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedCurioRenderers {
    @SubscribeEvent
    public static void registerCurioRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosRendererRegistry.register(ChangedItems.BENIGN_PANTS.get(), ShortsRenderer::new);
            CuriosRendererRegistry.register(ChangedItems.PINK_PANTS.get(), ShortsRenderer::new);
        });
    }
}
