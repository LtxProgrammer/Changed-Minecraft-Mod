package net.ltxprogrammer.changed.client.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.inventory.TextMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NoteScreen extends TextMenuScreen {
    public NoteScreen(TextMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override public int getBackgroundWidth() {
        return 87;
    }
    @Override public int getBackgroundHeight() {
        return 121;
    }
    @Override public int getTextAreaWidth() {
        return 77;
    }
    @Override public int getTextAreaHeight() {
        return 101;
    }
    @Override public int getTextAreaX() {
        return 5;
    }
    @Override public int getTextAreaY() {
        return 15;
    }

    @Override
    public ResourceLocation getBackground() {
        return Changed.modResource("textures/gui/note_container.png");
    }

    private static final Component NOTE = new TranslatableComponent("container.changed.note");
    @Override
    public Component getNoteTitle() {
        return NOTE;
    }
}
