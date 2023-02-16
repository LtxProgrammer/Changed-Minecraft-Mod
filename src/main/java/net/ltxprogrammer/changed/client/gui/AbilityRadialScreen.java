package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class AbilityRadialScreen extends LatexAbilityRadialScreen<AbilityRadialMenu> {
    private final static HashMap<String, Object> guistate = ComputerMenu.guistate;

    public final AbilityRadialMenu menu;
    public final LatexVariant<?> variant;
    public final List<ResourceLocation> abilities;

    public AbilityRadialScreen(AbilityRadialMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text, menu.variant);
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.menu = menu;
        this.variant = menu.variant;
        this.abilities = menu.variant.abilities;
    }

    @Override
    public int getCount() {
        return abilities.size();
    }

    @Nullable
    @Override
    public List<Component> tooltipsFor(int section) {
        return List.of(ChangedAbilities.getAbility(abilities.get(section)).getDisplayName(menu.player, menu.variant));
    }

    @Override
    public void renderSectionForeground(PoseStack pose, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue) {
        boolean enabled = false;
        if (menu.variant.abilityInstances.containsKey(abilities.get(section))) {
            enabled = menu.variant.abilityInstances.get(abilities.get(section)).canUse();
        }

        RenderSystem.setShaderTexture(0, ChangedAbilities.getAbility(abilities.get(section)).getTexture(menu.player, menu.variant));
        if (enabled) {
            RenderSystem.setShaderColor(0, 0, 0, 0.5f); // Render ability shadow
            this.blit(pose, (int)x - 24 + this.leftPos, (int)y - 24 + this.topPos + 4, 0, 0, 48, 48, 48, 48);
        }
        float minRed = Math.max(red, 0.125f);
        float minGreen = Math.max(green, 0.125f);
        float minBlue = Math.max(blue, 0.125f);
        RenderSystem.setShaderColor(minRed, minGreen, minBlue, enabled ? 1 : 0.5f);
        this.blit(pose, (int)x - 24 + this.leftPos, (int)y - 24 + this.topPos, 0, 0, 48, 48, 48, 48);
    }

    @Override
    public boolean handleClicked(int section, SingleRunnable close) {
        close.run();
        Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(abilities.get(section)));
        return false;
    }
}