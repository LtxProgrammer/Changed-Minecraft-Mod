package net.ltxprogrammer.changed.client.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class ComputerTextScreen extends TextMenuScreen<ComputerMenu> {
    public static final TranslatableComponent TITLE = new TranslatableComponent("container.changed.computer_writing");

    public final Screen previous;
    public ComputerTextScreen(Screen previous, ComputerMenu container, Inventory inventory, Component text) {
        super(container, inventory.player, text);
        this.previous = previous;
    }

    @Override public int getBackgroundWidth() {
        return 176;
    }
    @Override public int getBackgroundHeight() {
        return 166;
    }
    @Override public int getTextAreaWidth() {
        return 156;
    }
    @Override public int getTextAreaHeight() {
        return 134;
    }
    @Override public int getTextAreaX() {
        return 10;
    }
    @Override public int getTextAreaY() {
        return 22;
    }
    @Override public int getTextColor() {
        return 0x00ff00;
    }

    @Override
    public ResourceLocation getBackground() {
        return Changed.modResource("textures/gui/computer.png");
    }

    @Nullable
    @Override
    public Component getNoteTitle() {
        return TITLE;
    }
}