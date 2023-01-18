package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.item.FloppyDisk;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.util.TextUtil;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class AbilityRadialScreen extends AbstractContainerScreen<AbilityRadialMenu> {
    private final static HashMap<String, Object> guistate = ComputerMenu.guistate;

    public final AbilityRadialMenu menu;
    public final LatexVariant<?> variant;
    public final List<ResourceLocation> abilities;
    private int tickCount = 0;
    private final ChangedParticles.Color3 primaryColor;
    private final ChangedParticles.Color3 secondaryColor;

    public AbilityRadialScreen(AbilityRadialMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text);
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.menu = menu;
        this.variant = menu.variant;
        this.abilities = menu.variant.abilities;

        var colors = getColors(variant);
        this.primaryColor = colors.getA();
        this.secondaryColor = colors.getB();
    }

    public static Pair<ChangedParticles.Color3, ChangedParticles.Color3> getColors(LatexVariant<?> variant) {
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
    public ResourceLocation getAbilityAt(int mouseX, int mouseY) {
        for (int sect = 0; sect < 8 && sect < abilities.size() && sect <= tickCount; sect++) {
            double dbl = (sect + 0.5 + calcOffset(sect)) / 8.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);

            int minX = x - 24 + this.leftPos;
            int maxX = minX + 48;
            int minY = y - 24 + this.topPos;
            int maxY = minY + 48;
            if (mouseX >= minX && mouseX <= maxX &&
                mouseY >= minY && mouseY <= maxY)
                return abilities.get(sect);
        }

        return null;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        this.renderBg(ms, partialTicks, mouseX, mouseY);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        var hoveredAbility = getAbilityAt(mouseX, mouseY);
        if (hoveredAbility != null)
            this.renderTooltip(ms, List.of(ChangedAbilities.getAbility(hoveredAbility).getDisplayName()), Optional.empty(), mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Render radial bg
        for (int sect = 0; sect < 8 && sect <= tickCount; sect++) {
            double dbl = (sect + 0.5 + calcOffset(sect)) / 8.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);

            boolean enabled = false;
            if (sect < abilities.size() && menu.variant.abilityInstances.containsKey(abilities.get(sect))) {
                enabled = menu.variant.abilityInstances.get(abilities.get(sect)).canUse();
            }

            RenderSystem.setShaderColor(primaryColor.red(), primaryColor.green(), primaryColor.blue(), enabled ? 1 : 0.5f);
            RenderSystem.setShaderTexture(0, Changed.modResource("textures/gui/radial/" + sect + ".png"));
            this.blit(ms, x - 32 + this.leftPos, y - 32 + this.topPos, 0, 0, 64, 64, 64, 64);
        }

        for (int sect = 0; sect < 8 && sect < abilities.size() && sect <= tickCount; sect++) {
            double dbl = (sect + 0.5 + calcOffset(sect)) / 8.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);

            boolean enabled = false;
            if (menu.variant.abilityInstances.containsKey(abilities.get(sect))) {
                enabled = menu.variant.abilityInstances.get(abilities.get(sect)).canUse();
            }

            RenderSystem.setShaderTexture(0, ChangedAbilities.getAbility(abilities.get(sect)).getTexture(menu.player, menu.variant));
            if (enabled) {
                RenderSystem.setShaderColor(0, 0, 0, 0.5f); // Render ability shadow
                this.blit(ms, x - 24 + this.leftPos, y - 24 + this.topPos + 4, 0, 0, 48, 48, 48, 48);
            }
            float minRed = Math.max(secondaryColor.red(), 0.125f);
            float minGreen = Math.max(secondaryColor.green(), 0.125f);
            float minBlue = Math.max(secondaryColor.blue(), 0.125f);
            RenderSystem.setShaderColor(minRed, minGreen, minBlue, enabled ? 1 : 0.5f);
            this.blit(ms, x - 24 + this.leftPos, y - 24 + this.topPos, 0, 0, 48, 48, 48, 48);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        InventoryScreen.renderEntityInInventory(this.leftPos, this.topPos + 50, 38, (float)(this.leftPos) - gx, (float)(this.topPos) - gy, variant.getLatexEntity());

        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        var ability = getAbilityAt((int)p_97748_, (int)p_97749_);
        if (ability != null && menu.variant.abilityInstances.get(ability).canUse()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.player.closeContainer();
            Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(ability));
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
            return true;
        }

        if (key == GLFW.GLFW_KEY_0) { // Due to keyboard positioning, 0 will be treated as 10
            if (10 < abilities.size()) {
                this.minecraft.player.closeContainer();
                Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(abilities.get(10)));
                return true;
            }
        }

        if (key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_9) {
            int idx = key - GLFW.GLFW_KEY_1;
            if (idx < abilities.size()) {
                this.minecraft.player.closeContainer();
                Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(abilities.get(idx)));
                return true;
            }
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        tickCount++;
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