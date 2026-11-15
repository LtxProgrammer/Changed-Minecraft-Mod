package net.ltxprogrammer.changed.init;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ChangedItemProperties {
    public static void registerProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ChangedItems.TSC_SHIELD.get(), ResourceLocation.parse("blocking"), (item, level, entity, i) -> {
                return entity != null && entity.isUsingItem() && entity.getUseItem() == item ? 1.0F : 0.0F;
            });
        });
    }
}
