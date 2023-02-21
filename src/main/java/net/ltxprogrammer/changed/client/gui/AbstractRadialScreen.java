package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRadialScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
    private final static HashMap<String, Object> guistate = ComputerMenu.guistate;

    public final T menu;
    private int tickCount = 0;
    private int viewOffsetO = 0;
    private int viewOffset = 0;
    private int viewTransitionTicksLeft = 0;
    private static final int VIEW_TRANSITION_LENGTH = 8;

    private float getViewOffset() {
        return Mth.lerp((float)viewTransitionTicksLeft / (float)VIEW_TRANSITION_LENGTH, (float)viewOffset, (float)viewOffsetO);
    }

    private final ChangedParticles.Color3 primaryColor;
    private final ChangedParticles.Color3 secondaryColor;
    public final LivingEntity centerEntity;

    public AbstractRadialScreen(T menu, Inventory inventory, Component text, ChangedParticles.Color3 primary, ChangedParticles.Color3 secondary) {
        this(menu, inventory, text, primary, secondary, Minecraft.getInstance().player);
    }

    public AbstractRadialScreen(T menu, Inventory inventory, Component text, ChangedParticles.Color3 primary, ChangedParticles.Color3 secondary, LivingEntity centerEntity) {
        super(menu, inventory, text);
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.menu = menu;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
        this.centerEntity = centerEntity;
    }

    public abstract int getCount();

    public static Pair<ChangedParticles.Color3, ChangedParticles.Color3> getColors(LatexVariant<?> variant) {
        if (variant.getLatexEntity() instanceof SpecialLatex specialLatex && specialLatex.specialLatexForm != null) {
            return new Pair<>(
                    specialLatex.getCurrentData().primaryColor(),
                    specialLatex.getCurrentData().secondaryColor()
            );
        }

        var ints = ChangedEntities.getEntityColor(variant.getEntityType().getRegistryName());
        return new Pair<>(
                ChangedParticles.Color3.fromInt(ints.getA()),
                ChangedParticles.Color3.fromInt(ints.getB()));
    }

    private static final double RADIAL_DISTANCE = 90.0;

    private static double calcOffset(int section) {
        return section % 2 == 0 ? 0.04 : -0.04;
    }

    @Nullable
    public Optional<Integer> getSectionAt(int mouseX, int mouseY) {
        double off = getViewOffset();
        int offFloor = (int)Math.floor(getViewOffset());
        int offCiel = (int)Math.ceil(getViewOffset());
        double offFrac = Mth.frac(off);
        for (int sect = offFloor; sect < 8 + offCiel && sect < getCount() && sect <= tickCount; sect++) {
            double dbl = (sect - offFrac + 0.5 + calcOffset(sect - offFloor) - offFloor) / 8.0;
            double anim;
            if (sect < offCiel) {
                anim = 1.0 + (off - sect);
                dbl = 0.0625;
            } else if (sect >= 8 + offFloor) {
                anim = 1.0 + (1.0 - ((8 + off) - sect));
                dbl = 0.9375;
            } else
                anim = 1.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE * anim);

            int minX = x - 24 + this.leftPos;
            int maxX = minX + 48;
            int minY = y - 24 + this.topPos;
            int maxY = minY + 48;
            if (mouseX >= minX && mouseX <= maxX &&
                mouseY >= minY && mouseY <= maxY)
                return Optional.of(sect);
        }

        return Optional.empty();
    }

    public abstract @Nullable List<Component> tooltipsFor(int section);

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        this.renderBg(ms, partialTicks, mouseX, mouseY);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        getSectionAt(mouseX, mouseY).ifPresent(section -> {
            var toolTips = tooltipsFor(section);
            if (toolTips != null)
                this.renderTooltip(ms, toolTips, Optional.empty(), mouseX, mouseY);
        });
    }

    public abstract void renderSectionBackground(PoseStack pose, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue);
    public abstract void renderSectionForeground(PoseStack pose, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue, float alpha);

    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Render radial bg
        for (int sect = 0; sect < 8 && sect <= tickCount; sect++) {
            double dbl = (sect + 0.5 + calcOffset(sect)) / 8.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);

            renderSectionBackground(ms, sect, x, y, partialTicks, gx, gy, primaryColor.red(), primaryColor.green(), primaryColor.blue());
        }

        double off = getViewOffset();
        int offFloor = (int)Math.floor(getViewOffset());
        int offCiel = (int)Math.ceil(getViewOffset());
        double offFrac = Mth.frac(off);
        for (int sect = offFloor; sect < 8 + offCiel && sect < getCount() && sect <= tickCount; sect++) {
            double dbl = (sect - offFrac + 0.5 + calcOffset(sect - offFloor) - offFloor) / 8.0;
            double anim;
            if (sect < offCiel) {
                anim = 1.0 + (off - sect);
                dbl = 0.0625;
            } else if (sect >= 8 + offFloor) {
                anim = 1.0 + (1.0 - ((8 + off) - sect));
                dbl = 0.9375;
            } else
                anim = 1.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE * anim);

            renderSectionForeground(ms, sect, x, y, partialTicks, gx, gy, secondaryColor.red(), secondaryColor.green(), secondaryColor.blue(), (float)anim);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        InventoryScreen.renderEntityInInventory(this.leftPos, this.topPos + 50, 38, (float)(this.leftPos) - gx, (float)(this.topPos) - gy, centerEntity);

        RenderSystem.disableBlend();
    }

    /**
     * Called when a section is clicked/keyed
     * @param section section index
     * @param close function that will safely close the menu (closes only once)
     * @return True if the radial screen should accept the key/click
     */
    public abstract boolean handleClicked(int section, SingleRunnable close);

    public void scrollDown() {
        if (viewOffset + 8 < getCount()) {
            viewOffsetO = viewOffset;
            viewOffset++;
            viewTransitionTicksLeft = VIEW_TRANSITION_LENGTH;
        }
    }

    public void scrollUp() {
        if (viewOffset > 0) {
            viewOffsetO = viewOffset;
            viewOffset--;
            viewTransitionTicksLeft = VIEW_TRANSITION_LENGTH;
        }
    }

    @Override
    public boolean mouseScrolled(double scrollX, double scrollY, double scrollDelta) {
        if (scrollDelta < 0.01 && viewTransitionTicksLeft <= 0)
            scrollDown();
        else if (scrollDelta > 0.01 && viewTransitionTicksLeft <= 0)
            scrollUp();
        return true;
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        var section = getSectionAt((int)p_97748_, (int)p_97749_);
        if (section.isPresent()) {
            SingleRunnable single = new SingleRunnable(this.minecraft.player::closeContainer);
            if (handleClicked(section.get(), single)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                single.run();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
            return true;
        }

        if (key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_9) {
            int idx = key - GLFW.GLFW_KEY_1 + viewOffset;
            SingleRunnable single = new SingleRunnable(this.minecraft.player::closeContainer);
            if (idx < getCount() && handleClicked(idx, single)) {
                single.run();
                return true;
            }
        }

        if (key == GLFW.GLFW_KEY_RIGHT) {
            scrollDown();
        }

        if (key == GLFW.GLFW_KEY_LEFT) {
            scrollUp();
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        tickCount++;
        if (viewTransitionTicksLeft > 0)
            viewTransitionTicksLeft--;
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