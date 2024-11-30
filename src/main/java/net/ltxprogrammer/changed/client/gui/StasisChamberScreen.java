package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StasisChamberScreen extends AbstractContainerScreen<StasisChamberMenu> {
    private final StasisChamberMenu menu;

    public StasisChamberScreen(StasisChamberMenu menu, Inventory inventory, Component text) {
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

        float progress = menu.getFluidLevel(partialTicks);
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
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }

    private boolean initialized = false;
    private Button openDoorButton;
    private Button closeDoorButton;
    private Button runButton;
    private Button captureNextEntityButton;
    private Button toggleStasisButton;

    private void checkButtonStates() {
        if (!initialized) return;

        boolean open = menu.isChamberOpen();
        openDoorButton.active = !open;
        closeDoorButton.active = open;
        captureNextEntityButton.active = open;
        runButton.active = !open;
        toggleStasisButton.active = !open;
    }

    @Override
    public void containerTick() {
        super.containerTick();

        checkButtonStates();
    }

    @Override
    public void init() {
        super.init();

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        openDoorButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6, 100, 20, new TranslatableComponent("changed.stasis.open"), button -> {
            menu.sendSimpleCommand(StasisChamberMenu.Command.OPEN_DOOR);
        }));

        closeDoorButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 24, 100, 20, new TranslatableComponent("changed.stasis.close"), button -> {
            menu.sendSimpleCommand(StasisChamberMenu.Command.CLOSE_DOOR);
        }));

        runButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 24 * 2, 100, 20, new TranslatableComponent("changed.stasis.run"), button -> {

        }));

        captureNextEntityButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 24 * 3, 100, 20, new TranslatableComponent("changed.stasis.capture_next"), button -> {

        }));

        toggleStasisButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 24 * 4, 100, 20, new TranslatableComponent("changed.stasis.toggle_stasis"), button -> {

        }));

        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 24 * 5, 100, 20, new TranslatableComponent("changed.stasis.modify"), button -> {
            this.minecraft.setScreen(new StasisChamberModifyScreen(menu, this, menu.getChamberedLatex().orElse(null)));
        }));

        initialized = true;

        checkButtonStates();
    }
}