package net.ltxprogrammer.changed.client.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public abstract class VariantRadialScreen<T extends AbstractContainerMenu> extends AbstractRadialScreen<T> {
    public VariantRadialScreen(T menu, Inventory inventory, Component text, TransfurVariantInstance<?> variant) {
        super(menu, inventory, text, getColors(variant).setForegroundToBright().background(),
                getColors(variant).setForegroundToBright().foreground(), variant.getHost());
    }

    private static final ResourceLocation PATH_ORGANIC = Changed.modResource("textures/gui/radial/organic.png");
    private static final ResourceLocation PATH_GOO = Changed.modResource("textures/gui/radial/goo.png");
    private static final ResourceLocation PATH_ORGANIC_SELECTED = Changed.modResource("textures/gui/radial/organic_selected.png");
    private static final ResourceLocation PATH_GOO_SELECTED = Changed.modResource("textures/gui/radial/goo_selected.png");
    private static final ResourceLocation PATH_ORGANIC_HOVERED = Changed.modResource("textures/gui/radial/organic_hovered.png");
    private static final ResourceLocation PATH_GOO_HOVERED = Changed.modResource("textures/gui/radial/goo_hovered.png");

    public abstract boolean isSelected(int section);

    protected int getSectionU(int section) {
        return (section % 4) * 80;
    }

    protected int getSectionV(int section) {
        return (section / 4) * 80;
    }

    protected ResourceLocation getTextureForSection(@Nullable TransfurVariant<?> variant, int section, boolean thisHovered, boolean anyHovered) {
        if (variant == null || variant.getEntityType().is(ChangedTags.EntityTypes.LATEX))
            return thisHovered ? PATH_GOO_HOVERED : PATH_GOO;
        else
            return thisHovered ? PATH_ORGANIC_HOVERED : PATH_ORGANIC;
    }

    protected ResourceLocation getTextureForSelectedSection(@Nullable TransfurVariant<?> variant) {
        if (variant == null || variant.getEntityType().is(ChangedTags.EntityTypes.LATEX))
            return PATH_GOO_SELECTED;
        else
            return PATH_ORGANIC_SELECTED;
    }

    @Override
    public void renderSectionBackground(GuiGraphics graphics, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue) {
        var hovered = getSectionAt(mouseX, mouseY);
        var variant = TransfurVariant.getEntityVariant((LivingEntity) Minecraft.getInstance().getCameraEntity());
        boolean anyHovered = hovered.isPresent();
        boolean thisHovered = anyHovered && hovered.get() == section;
        boolean thisSelected = this.isSelected(section);
        int u = this.getSectionU(section);
        int v = this.getSectionV(section);
        graphics.setColor(red, green, blue, 1);
        graphics.blit(getTextureForSection(variant, section, thisHovered, anyHovered),
                (int)x - 32 + this.leftPos - 8, (int)y - 32 + this.topPos - 8, u, v, 80, 80, 320, 160);
        graphics.setColor(1, 1, 1, 1);

        if (!thisSelected)
            return;

        graphics.blit(getTextureForSelectedSection(variant),
                (int)x - 32 + this.leftPos - 8, (int)y - 32 + this.topPos - 8, u, v, 80, 80, 320, 160);
    }
}
