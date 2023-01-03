package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.PurifierBlockEntity;
import net.ltxprogrammer.changed.world.inventory.PurifierMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PurifierScreen extends AbstractContainerScreen<PurifierMenu> {
    private final PurifierMenu menu;

    public PurifierScreen(PurifierMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text);
        this.menu = menu;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private static final ResourceLocation texture = Changed.modResource("textures/gui/purifier.png");

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, texture);
        blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        RenderSystem.setShaderTexture(0, Changed.modResource("textures/gui/progress_bar_back.png"));
        blit(ms, this.leftPos + 63, this.topPos + 62, 0, 0, 48, 12, 48, 12);

        float progress = (float)menu.data.get(0) / (float) PurifierBlockEntity.PURIFY_PROGRESS_TOTAL;
        RenderSystem.setShaderTexture(0, Changed.modResource("textures/gui/progress_bar_front.png"));
        blit(ms, this.leftPos + 63, this.topPos + 62, 0, 0, (int)(48 * progress), 12, 48, 12);

        RenderSystem.disableBlend();
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
    public void containerTick() {
        super.containerTick();
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void init() {
        super.init();

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

    }

}