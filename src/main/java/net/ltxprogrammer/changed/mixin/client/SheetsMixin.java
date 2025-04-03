package net.ltxprogrammer.changed.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.entity.variant.ClothingShape;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

@Mixin(Sheets.class)
public abstract class SheetsMixin {
    @Unique
    private static void getMaterialFromSlot(@Nullable ResourceLocation slotTexture, Consumer<Material> next) {
        if (slotTexture != null)
            next.accept(new Material(InventoryMenu.BLOCK_ATLAS, slotTexture));
    }

    @Inject(method = "getAllMaterials", at = @At("HEAD"))
    private static void getAllMaterials(Consumer<Material> materialConsumer, CallbackInfo ci) {
        Arrays.stream(ClothingShape.Head.values())
                .map(ClothingShape.Head::getEmptyArmorSlot)
                .mapMulti(SheetsMixin::getMaterialFromSlot)
                .forEach(materialConsumer);
        Arrays.stream(ClothingShape.Torso.values())
                .map(ClothingShape.Torso::getEmptyArmorSlot)
                .mapMulti(SheetsMixin::getMaterialFromSlot)
                .forEach(materialConsumer);
        Arrays.stream(ClothingShape.Legs.values())
                .map(ClothingShape.Legs::getEmptyArmorSlot)
                .mapMulti(SheetsMixin::getMaterialFromSlot)
                .forEach(materialConsumer);
        Arrays.stream(ClothingShape.Feet.values())
                .map(ClothingShape.Feet::getEmptyArmorSlot)
                .mapMulti(SheetsMixin::getMaterialFromSlot)
                .forEach(materialConsumer);

        materialConsumer.accept(new Material(InventoryMenu.BLOCK_ATLAS, Changed.modResource("items/empty_slot_syringe")));
    }
}
