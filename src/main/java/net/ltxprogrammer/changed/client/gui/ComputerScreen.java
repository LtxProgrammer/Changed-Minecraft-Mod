package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.item.FloppyDisk;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.util.TextUtil;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class ComputerScreen extends AbstractContainerScreen<ComputerMenu> {
    private final static HashMap<String, Object> guistate = ComputerMenu.guistate;

    public final ComputerMenu container;

    public static final String ERR_CORRUPTED_DISK = "changed.error.corrupted_disk";
    public static final String TXT_WORKING = "menu.working";
    private int scroll = 0;
    private TextUtil.ScrollInfo scrollInfo;

    public ComputerScreen(ComputerMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.container = container;
    }

    private static final ResourceLocation texture = Changed.modResource("textures/gui/computer_v2.png");

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
        this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
            return true;
        }

        if (scrollInfo != null) {
            if (key == GLFW.GLFW_KEY_DOWN && scrollInfo.canScrollDown())
                scroll++;
            if (key == GLFW.GLFW_KEY_UP && scrollInfo.canScrollUp())
                scroll--;
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        Language lang = Language.getInstance();
        ItemStack disk = container.getDisk();
        String fallbackText = disk.is(Items.AIR) ? TXT_WORKING : ERR_CORRUPTED_DISK;
        this.font.draw(poseStack, lang.getOrDefault(TagUtil.getStringOrDefault(disk, FloppyDisk.TAG_TITLE, fallbackText)), 6, 7, -12829636);
        //this.font.draw(poseStack, lang.getOrDefault(TagUtil.getStringOrDefault(disk, FloppyDisk.TAG_DATA, fallbackText)), 6, 20, -16711936);
        scrollInfo = TextUtil.drawScrollWrapped(this.font, poseStack,
                lang.getOrDefault(TagUtil.getStringOrDefault(disk, FloppyDisk.TAG_DATA, fallbackText)),
                6, 20, -16711936, 164, 142, 0);
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