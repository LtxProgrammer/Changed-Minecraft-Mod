package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.item.CompactDisc;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.util.TextUtil;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.TextMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class ComputerScreen extends TextMenuScreen {
    public ComputerScreen(TextMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
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
        return Changed.modResource("textures/gui/computer_v2.png");
    }

    private static final TranslatableComponent title = new TranslatableComponent("container.changed.computer_writing");

    @Nullable
    @Override
    public Component getNoteTitle() {
        return title;
    }
}