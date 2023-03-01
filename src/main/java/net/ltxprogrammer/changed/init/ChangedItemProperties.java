package net.ltxprogrammer.changed.init;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedItemProperties {
    @SubscribeEvent
    public static void registerProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ChangedItems.TSC_SHIELD.get(), new ResourceLocation("blocking"), (item, level, entity, i) -> {
                return entity != null && entity.isUsingItem() && entity.getUseItem() == item ? 1.0F : 0.0F;
            });
        });
    }
}
