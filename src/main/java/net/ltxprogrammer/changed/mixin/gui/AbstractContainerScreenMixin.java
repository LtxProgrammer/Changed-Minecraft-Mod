package net.ltxprogrammer.changed.mixin.gui;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.SlotWrapper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
    private static final Map<ResourceLocation, Pair<ResourceLocation, ResourceLocation>> ABDOMEN_SLOT_OVERRIDES = ImmutableMap.of(
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, ChangedTextures.EMPTY_ARMOR_SLOT_UPPER_ABDOMEN,
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, ChangedTextures.EMPTY_ARMOR_SLOT_LOWER_ABDOMEN
    );
    @Unique
    private static final Map<ResourceLocation, Pair<ResourceLocation, ResourceLocation>> QUADRUPEDAL_SLOT_OVERRIDES = ImmutableMap.of(
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, ChangedTextures.EMPTY_ARMOR_SLOT_QUADRUPEDAL_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, ChangedTextures.EMPTY_ARMOR_SLOT_QUADRUPEDAL_BOOTS
    );

    @Unique
    @Nullable
    private static Map<ResourceLocation, Pair<ResourceLocation, ResourceLocation>> getOverridesForVariant(TransfurVariant<?> variant) {
        if (variant.legCount == 0)
            return ABDOMEN_SLOT_OVERRIDES;
        else if (variant.legCount == 4)
            return QUADRUPEDAL_SLOT_OVERRIDES;
        return null;
    }

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    public void renderSlot(PoseStack pose, Slot slot, CallbackInfo callback) {
        if (slot instanceof SlotWrapper)
            return; // Process as normal

        if ((Screen)this instanceof EffectRenderingInventoryScreen<?> inventoryScreen) {
            InventoryMenu menu = null;
            if (inventoryScreen.getMenu() instanceof CreativeModeInventoryScreen.ItemPickerMenu pickerMenu)
                menu = (InventoryMenu)pickerMenu.inventoryMenu;
            else if (inventoryScreen.getMenu() instanceof InventoryMenu invMenu)
                menu = invMenu;

            if (menu != null) {
                ProcessTransfur.ifPlayerTransfurred(menu.owner, variant -> {
                    var originalPair = slot.getNoItemIcon();
                    var overrides = getOverridesForVariant(variant.getParent());
                    if (overrides != null && originalPair != null && overrides.containsKey(originalPair.getSecond())) {
                        callback.cancel();
                        renderSlot(pose, new SlotWrapper(slot, slot.getSlotIndex(), slot.x, slot.y, overrides.get(originalPair.getSecond())));
                    }
                });
            }
        }
    }
}
