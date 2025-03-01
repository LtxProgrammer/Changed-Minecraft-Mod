package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.accessory.*;
import net.ltxprogrammer.changed.client.renderer.layers.AccessoryLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedAccessoryRenderers {
    @SubscribeEvent
    public static void registerCurioRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            AccessoryLayer.registerRenderer(ChangedItems.BENIGN_PANTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.PINK_PANTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.SPORTS_BRA.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
        });
    }
}
