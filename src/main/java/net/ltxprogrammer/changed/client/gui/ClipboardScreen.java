package net.ltxprogrammer.changed.client.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.inventory.TextMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ClipboardScreen extends TextMenuScreen {
    public ClipboardScreen(TextMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override public int getBackgroundWidth() {
        return 111;
    }
    @Override public int getBackgroundHeight() {
        return 151;
    }
    @Override public int getTextAreaWidth() {
        return 81;
    }
    @Override public int getTextAreaHeight() {
        return 91;
    }
    @Override public int getTextAreaX() {
        return 15;
    }
    @Override public int getTextAreaY() {
        return 40;
    }

    @Override
    public ResourceLocation getBackground() {
        return Changed.modResource("textures/gui/clipboard_container.png");
    }

    @Override
    public Component getNoteTitle() {
        return null;
    }
}
