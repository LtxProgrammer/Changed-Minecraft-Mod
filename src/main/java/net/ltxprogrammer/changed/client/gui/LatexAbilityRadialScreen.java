package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class LatexAbilityRadialScreen<T extends AbstractContainerMenu> extends AbstractRadialScreen<T> {
    public LatexAbilityRadialScreen(T menu, Inventory inventory, Component text, LatexVariantInstance<?> variant) {
        super(menu, inventory, text, getColors(variant).getFirst(), getColors(variant).getSecond(), variant.getLatexEntity());
    }

    @Override
    public void renderSectionBackground(PoseStack pose, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue) {
        RenderSystem.setShaderColor(red, green, blue, 1);
        RenderSystem.setShaderTexture(0, Changed.modResource("textures/gui/radial/" + section + ".png"));
        this.blit(pose, (int)x - 32 + this.leftPos, (int)y - 32 + this.topPos, 0, 0, 64, 64, 64, 64);
    }
}
