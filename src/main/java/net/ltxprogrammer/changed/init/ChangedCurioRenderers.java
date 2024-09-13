package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.curio.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.minecraft.world.entity.EquipmentSlot;
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
            CuriosRendererRegistry.register(ChangedItems.BENIGN_PANTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            CuriosRendererRegistry.register(ChangedItems.PINK_PANTS.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.LEGS));
            CuriosRendererRegistry.register(ChangedItems.SPORTS_BRA.get(), SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST));
        });
    }
}
