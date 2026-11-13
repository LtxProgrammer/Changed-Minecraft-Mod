package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.ltxprogrammer.changed.world.inventory.AccessoryAccessMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AccessoryAccessScreen extends EffectRenderingInventoryScreen<AccessoryAccessMenu> {
    private final AccessoryAccessMenu menu;
    private float xMouse;
    private float yMouse;
    private int textureWidth;
    private int textureHeight;
    private boolean extraHandsPanel;
    private @Nullable Runnable toolTip = null;

    public AccessoryAccessScreen(AccessoryAccessMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text);
        this.menu = menu;
        this.textureWidth = 176 + 18;
        this.textureHeight = 166;
        this.extraHandsPanel = menu.getExtraHandSlots() > 0;

        this.imageWidth = 176 + (extraHandsPanel ? 50 : 0);
        this.imageHeight = 166;
    }

    public void setToolTip(Runnable fn) {
        this.toolTip = fn;
    }

    private static final ResourceLocation TEXTURE = Changed.modResource("textures/gui/accessories.png");
    private static final ResourceLocation TEXTURE_EXTRA_HANDS = Changed.modResource("textures/gui/extra_hands.png");

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        this.xMouse = (float)mouseX;
        this.yMouse = (float)mouseY;

        this.renderTooltip(graphics, mouseX, mouseY);

        if (toolTip != null) {
            if (this.menu.getCarried().isEmpty()) {
                toolTip.run();
            }
            toolTip = null;
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int gx, int gy) {
        graphics.setColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int leftOffset = extraHandsPanel ? 50 : 0;

        graphics.blit(TEXTURE, this.leftPos + leftOffset, this.topPos, 0, 0, 176, 166, this.textureWidth, this.textureHeight);
        if (extraHandsPanel)
            graphics.blit(TEXTURE_EXTRA_HANDS, this.leftPos, this.topPos, 0, 0, 50, 166, 68, 166);

        AtomicInteger slotIndex = new AtomicInteger(0);
        menu.getBuiltSlots().forEach(slotType -> {
            var slot = menu.getCustomSlot(slotIndex.getAndAdd(1));
            if (slot == null)
                return;

            final List<ItemStack> conflictingItems = menu.getBuiltSlots().stream()
                    .filter(otherSlotType -> otherSlotType != slotType)
                    .<ItemStack>map(otherSlotType -> {
                        final var context = AccessorySlotContext.of(menu.owner, otherSlotType);
                        if (context.stack().getItem() instanceof AccessoryItem accessoryItem &&
                                accessoryItem.shouldDisableSlot(context, slotType))
                            return context.stack();
                        return null;
                    }).filter(Objects::nonNull).toList();

            if (conflictingItems.isEmpty())
                graphics.blit(TEXTURE, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, 176, 0, 18, 18, this.textureWidth, this.textureHeight);
            else {
                graphics.blit(TEXTURE, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, 176, 18, 18, 18, this.textureWidth, this.textureHeight);

                if (slot == this.hoveredSlot) {
                    setToolTip(() -> {
                        final var rows = new ArrayList<Component>();
                        rows.add(Component.translatable("changed.accessory.slot_disabled_by"));
                        conflictingItems.forEach(stack -> rows.add(stack.getHoverName()));

                        graphics.renderTooltip(this.font, rows, Optional.empty(), gx, gy);
                    });
                }
            }
        });

        for (int extraHandSlot = 0; extraHandSlot < menu.getExtraHandSlots(); ++extraHandSlot) {
            var slot = menu.getCustomSlot(extraHandSlot + slotIndex.getAcquire());
            if (slot == null)
                return;

            graphics.blit(TEXTURE_EXTRA_HANDS, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, 50, 0, 18, 18, 68, 166);
        }

        RenderSystem.disableBlend();

        int i = this.leftPos;
        int j = this.topPos;
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, i + 51 + leftOffset, j + 75, 30, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.minecraft.player);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    protected void slotClicked(@Nullable Slot slot, int index, int button, @NotNull ClickType clickType) {
        if (slot != null && button == this.minecraft.options.keyUse.getKey().getValue()) {
            if (slot.container instanceof AccessorySlots accessorySlots) {
                AccessorySlotType slotType = accessorySlots.getSlotTypeByIndex(slot.getSlotIndex());
                if (slotType != null) {
                    AccessorySlots.onInteractAccessory(menu.owner, slotType);
                    return;
                }
            }
        }

        super.slotClicked(slot, index, button, clickType);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }
}