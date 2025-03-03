package net.ltxprogrammer.changed.mixin.gui;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.AccessoryAccessMenu;
import net.ltxprogrammer.changed.world.inventory.SlotWrapper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Shadow protected abstract void renderSlot(PoseStack p_97800_, Slot p_97801_);

    @Unique
    private static final Map<ResourceLocation, EquipmentSlot> REVERSE_MAP = ImmutableMap.of(
            InventoryMenu.EMPTY_ARMOR_SLOT_HELMET, EquipmentSlot.HEAD,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, EquipmentSlot.CHEST,
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, EquipmentSlot.LEGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, EquipmentSlot.FEET
    );

    @Unique
    @Nullable
    private static Pair<ResourceLocation, ResourceLocation> createMaterialFrom(@Nullable ResourceLocation texture) {
        return texture == null ? null : Pair.of(InventoryMenu.BLOCK_ATLAS, texture);
    }

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    public void renderSlot(PoseStack pose, Slot slot, CallbackInfo callback) {
        if (slot instanceof SlotWrapper)
            return; // Process as normal

        if (slot.container instanceof Inventory inventory) {
            ProcessTransfur.ifPlayerTransfurred(inventory.player, variant -> {
                var material = slot.getNoItemIcon();
                if (material == null)
                    return;

                var entityShape = variant.getChangedEntity().getEntityShape();
                if (!REVERSE_MAP.containsKey(material.getSecond()))
                    return;

                material = switch (REVERSE_MAP.get(material.getSecond())) {
                    case HEAD -> createMaterialFrom(entityShape.getHeadShape().getEmptyArmorSlot());
                    case CHEST -> createMaterialFrom(entityShape.getTorsoShape().getEmptyArmorSlot());
                    case LEGS -> createMaterialFrom(entityShape.getLegsShape().getEmptyArmorSlot());
                    case FEET -> createMaterialFrom(entityShape.getFeetShape().getEmptyArmorSlot());
                    default -> null;
                };

                if (material != null) {
                    callback.cancel();
                    renderSlot(pose, new SlotWrapper(slot, slot.getSlotIndex(), slot.x, slot.y, material));
                }
            });
        }
    }
}
