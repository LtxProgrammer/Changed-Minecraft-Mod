package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.SpecialStateRadialMenu;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpecialStateRadialScreen extends LatexAbilityRadialScreen<SpecialStateRadialMenu> {
    public final SpecialStateRadialMenu menu;
    public final SpecialLatex special;
    public final List<String> states;

    public SpecialStateRadialScreen(SpecialStateRadialMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text, menu.variant);
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.menu = menu;
        if (menu.variant.getLatexEntity() instanceof SpecialLatex specialLatex)
            this.special = specialLatex;
        else
            throw new IllegalArgumentException("Variant is not special latex!");
        this.states = new ArrayList<>(specialLatex.specialLatexForm.entityData().keySet());
    }

    @Override
    public int getCount() {
        return states.size();
    }

    @Nullable
    @Override
    public List<Component> tooltipsFor(int section) {
        return null;
    }

    @Override
    public void renderSectionForeground(PoseStack pose, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue, float alpha) {
        x = x * 0.9;
        y = (y * 0.9) - 16;

        var oldState = special.wantedState;
        special.wantedState = states.get(section);
        InventoryScreen.renderEntityInInventory((int)x + this.leftPos, (int)y + 32 + this.topPos, 20,
                (float)(this.leftPos) - mouseX + (int)x,
                (float)(this.topPos) - mouseY + (int)y,
                special);
        special.wantedState = oldState;
    }

    @Override
    public boolean handleClicked(int section, SingleRunnable close) {
        this.special.wantedState = states.get(section);
        ChangedAbilities.getAbility(ChangedAbilities.SELECT_SPECIAL_STATE.getId()).setDirty(this.menu.player, this.menu.variant);
        return true;
    }
}