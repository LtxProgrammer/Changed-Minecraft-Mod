package net.ltxprogrammer.changed.client.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.computer.*;
import net.ltxprogrammer.changed.computers.LexicalPath;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.init.ChangedApplications;
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
import org.jetbrains.annotations.Nullable;

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
        setFocused((GuiEventListener) null);
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

    protected @Nullable ApplicationScreen getTopApplication() {
        if (!this.applicationScreens.empty())
            return this.applicationScreens.peek();
        return null;
    }

    @Override
    public void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.setColor(1, 1, 1, 1);
        graphics.blit(getBorder(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        graphics.blit(getBackground(), this.leftPos+6, this.topPos+6, 0, 0,
                this.imageWidth - 12, this.imageHeight - 12, this.imageWidth - 12, this.imageHeight - 12);

        var top = getTopApplication();
        if (top != null)
            top.render(graphics, cursorX, cursorY, partialTicks);
        super.render(graphics, cursorX, cursorY, partialTicks);
    }

    @Override
    public void tick() {
        var top = getTopApplication();
        if (top != null)
            top.tick(this.leftPos + 6, this.topPos + 6, this.imageWidth - 12, this.imageHeight - 12);
        super.tick();
    }

    @Override
    public void mouseMoved(double x, double y) {
        var top = getTopApplication();
        if (top != null)
            top.mouseMoved(x, y);
        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        var top = getTopApplication();
        if (top != null && top.mouseClicked(x, y, button))
            return true;
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        var top = getTopApplication();
        if (top != null && top.mouseReleased(x, y, button))
            return true;
        return super.mouseReleased(x, y, button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        var top = getTopApplication();
        if (top != null && top.mouseDragged(x, y, button, dx, dy))
            return true;
        return super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double yOffset) {
        var top = getTopApplication();
        if (top != null && top.mouseScrolled(x, y, yOffset))
            return true;
        return super.mouseScrolled(x, y, yOffset);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        var top = getTopApplication();
        if (top != null && top.keyPressed(key, scanCode, modifiers))
            return true;
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scanCode, int modifiers) {
        var top = getTopApplication();
        if (top != null && top.keyReleased(key, scanCode, modifiers))
            return true;
        return super.keyReleased(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        var top = getTopApplication();
        if (top != null && top.charTyped(c, modifiers))
            return true;
        return super.charTyped(c, modifiers);
    }

    @Override
    public void removed() {
        while (!this.applicationScreens.empty())
            this.popApplicationScreen();
        super.removed();
    }

    public void openFile(LexicalPath.Absolute fullPath) {
        this.menu.computer.getFile(fullPath).ifLeft(file -> {
            switch (file.type) {
                case APP -> {
                    var app = ChangedRegistry.APPLICATION_TYPES.getValue(ResourceLocation.parse(file.getContent()));
                    Changed.PACKET_HANDLER.sendToServer(ComputerAppLaunchPacket.launchApplication(app));
                }
                //case PICTURE -> {} // TODO open window with picture
                case TEXT -> Changed.PACKET_HANDLER.sendToServer(ComputerAppLaunchPacket.launchApplication(ChangedApplications.TEXT_EDITOR.get(), fullPath.toString()));
                case RECIPE -> this.menu.setDirty(this.menu.requestRecipe(fullPath));
                default -> {} // No action
            }
        });
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.player.closeContainer();
    }
}