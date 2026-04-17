package net.ltxprogrammer.changed.client.gui;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.computer.*;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.ApplicationType;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.ComputerAppLaunchPacket;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ComputerScreen extends Screen implements MenuAccess<ComputerMenu> {
    protected final ComputerMenu menu;
    protected int imageWidth, imageHeight;
    protected int leftPos, topPos;
    protected Inventory inventory;

    protected UITheme theme = UITheme.DEFAULT;
    protected final Stack<ApplicationScreen> applicationScreens = new Stack<>();

    public ComputerScreen(ComputerMenu container, Inventory inventory, Component text) {
        super(text);
        this.menu = container;
        this.inventory = inventory;
    }

    public UITheme getTheme() {
        return theme;
    }

    @Override
    public ComputerMenu getMenu() {
        return menu;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected List<GuiEventListener> widgets = new ArrayList<>();

    public <T extends AbstractWidget> T addApplicationWidget(T widget) {
        widgets.add(this.addRenderableWidget(widget));
        return widget;
    }

    public void clearApplicationWidgets() {
        widgets.forEach(this::removeWidget);
        widgets.clear();
    }

    @Override
    protected void init() {
        super.init();

        this.imageWidth = 332;
        this.imageHeight = 212;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.pushApplicationScreen(ApplicationScreens.createScreen(menu.currentApplication(), this));

        menu.setWorkingDir(menu.getDesktopDir());
    }

    public void pushApplicationScreen(ApplicationScreen applicationScreen) {
        this.applicationScreens.push(applicationScreen).initialize(this.leftPos + 6, this.topPos + 6, this.imageWidth - 12, this.imageHeight - 12);
        applicationScreen.opened();
    }

    public void popApplicationScreen() {
        this.applicationScreens.pop().closed();
        if (this.applicationScreens.empty())
            return;

        var restoredScreen = this.applicationScreens.peek();
        restoredScreen.initialize(this.leftPos + 6, this.topPos + 6, this.imageWidth - 12, this.imageHeight - 12);
        restoredScreen.restored();
    }

    public ResourceLocation getBorder() {
        return Changed.modResource("textures/gui/computer/border.png");
    }

    public ResourceLocation getBackground() {
        return theme.desktopBackground();
    }

    @Override
    public void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.setColor(1, 1, 1, 1);
        graphics.blit(getBorder(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        graphics.blit(getBackground(), this.leftPos+6, this.topPos+6, 0, 0,
                this.imageWidth - 12, this.imageHeight - 12, this.imageWidth - 12, this.imageHeight - 12);

        if (!this.applicationScreens.empty())
            this.applicationScreens.peek().render(graphics, cursorX, cursorY, partialTicks);
        super.render(graphics, cursorX, cursorY, partialTicks);
    }

    @Override
    public void tick() {
        if (!this.applicationScreens.empty())
            this.applicationScreens.peek().tick(this.leftPos + 6, this.topPos + 6, this.imageWidth - 12, this.imageHeight - 12);
        super.tick();
    }

    @Override
    public void removed() {
        while (!this.applicationScreens.empty())
            this.popApplicationScreen();
        super.removed();
    }

    public void openFile(Path fullPath) {
        File file = this.menu.computer.getFile(fullPath);
        if (file == null) // 404
            return;

        switch (file.type) {
            case APP -> {
                var app = ChangedRegistry.APPLICATION_TYPES.getValue(ResourceLocation.parse(file.content));
                Changed.PACKET_HANDLER.sendToServer(ComputerAppLaunchPacket.launchApplication(app));
            }
            //case PICTURE -> {} // TODO open window with picture
            //case TEXT -> this.minecraft.setScreen(new ComputerTextScreen(/*this, */this.menu, this.inventory, ComputerTextScreen.TITLE));
            case RECIPE -> this.menu.setDirty(this.menu.requestRecipe(fullPath));
            default -> {} // No action
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.player.closeContainer();
    }
}