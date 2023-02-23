package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.init.ChangedTextures;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.SlotWrapper;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Shadow protected abstract void renderSlot(PoseStack p_97800_, Slot p_97801_);

    private static final Map<ResourceLocation, Pair<ResourceLocation, ResourceLocation>> ABDOMEN_SLOT_OVERRIDES = ImmutableMap.of(
            InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, ChangedTextures.EMPTY_ARMOR_SLOT_UPPER_ABDOMEN,
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, ChangedTextures.EMPTY_ARMOR_SLOT_LOWER_ABDOMEN
    );

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
                ProcessTransfur.ifPlayerLatex(menu.owner, variant -> {
                    var originalPair = slot.getNoItemIcon();
                    if (originalPair != null && !variant.getParent().hasLegs && ABDOMEN_SLOT_OVERRIDES.containsKey(originalPair.getSecond())) {
                        callback.cancel();
                        renderSlot(pose, new SlotWrapper(slot, slot.getSlotIndex(), slot.x, slot.y, ABDOMEN_SLOT_OVERRIDES.get(originalPair.getSecond())));
                    }
                });
            }
        }
    }
}
