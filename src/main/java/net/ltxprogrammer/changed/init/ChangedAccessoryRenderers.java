package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.accessory.*;
import net.ltxprogrammer.changed.client.renderer.layers.AccessoryLayer;
import net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModels;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedAccessoryRenderers {
    @SubscribeEvent
    public static void registerAccessoryRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var modelSet = Minecraft.getInstance().getEntityModels();

            AccessoryLayer.registerRenderer(ChangedItems.BENIGN_SHORTS.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.PINK_SHORTS.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.BLACK_PANTS.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.NAVY_PANTS.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.LEGS));
            AccessoryLayer.registerRenderer(ChangedItems.SPORTS_BRA.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.BLACK_TSHIRT.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.WHITE_TSHIRT.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.TSC_VEST.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.LAB_COAT.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_OUTER, Set.of(
                    new SimpleClothingRenderer.ModelComponent(ArmorModels.CLOTHING_OUTER, EquipmentSlot.CHEST),
                    new SimpleClothingRenderer.ModelComponent(ArmorModels.CLOTHING_MIDDLE, EquipmentSlot.LEGS)
            )));
            AccessoryLayer.registerRenderer(ChangedItems.WETSUIT.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, Set.of(
                    new SimpleClothingRenderer.ModelComponent(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST),
                    new SimpleClothingRenderer.ModelComponent(ArmorModels.CLOTHING_INNER, EquipmentSlot.LEGS)
            )));
            AccessoryLayer.registerRenderer(ChangedItems.NITRILE_GLOVES.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.ORANGE_NECK_TIE.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.RED_NECK_TIE.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.BLUE_NECK_TIE.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.DOG_COLLAR.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.CHEST));
            AccessoryLayer.registerRenderer(ChangedItems.FACE_MASK.get(), SimpleClothingRenderer.of(ArmorModels.CLOTHING_INNER, EquipmentSlot.HEAD));

            AccessoryLayer.registerRenderer(ChangedItems.EXOSKELETON.get(), () ->
                    new WornExoskeletonRenderer(modelSet, ExoskeletonModel.LAYER_LOCATION_SUIT, ExoskeletonModel.LAYER_LOCATION_VISOR, ExoskeletonModel.LAYER_LOCATION_HUMAN));

            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.BENIGN_SHORTS.get(), ChangedItems.PINK_SHORTS.get(), ChangedItems.SPORTS_BRA.get(),
                            ChangedItems.BLACK_PANTS.get(), ChangedItems.NAVY_PANTS.get()),
                    AccessoryLayer.RenderOrder.BELOW, ChangedItems.BLACK_TSHIRT.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.WHITE_TSHIRT.get(), ChangedItems.WETSUIT.get()),
                    AccessoryLayer.RenderOrder.SAME, ChangedItems.BLACK_TSHIRT.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.ORANGE_NECK_TIE.get(), ChangedItems.RED_NECK_TIE.get(), ChangedItems.BLUE_NECK_TIE.get(), ChangedItems.DOG_COLLAR.get(),
                            ChangedItems.FACE_MASK.get(), ChangedItems.TSC_VEST.get()),
                    AccessoryLayer.RenderOrder.ABOVE, ChangedItems.BLACK_TSHIRT.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.LAB_COAT.get()),
                    AccessoryLayer.RenderOrder.ABOVE, ChangedItems.ORANGE_NECK_TIE.get());
            AccessoryLayer.registerItemRenderOrder(
                    Set.of(ChangedItems.NITRILE_GLOVES.get()),
                    AccessoryLayer.RenderOrder.ABOVE, ChangedItems.LAB_COAT.get());
        });
    }
}
