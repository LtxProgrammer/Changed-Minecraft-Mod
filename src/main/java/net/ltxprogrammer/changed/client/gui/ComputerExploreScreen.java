package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ComputerExploreScreen extends Screen implements MenuAccess<ComputerMenu> {
    protected final ComputerMenu menu;
    protected int imageWidth, imageHeight;
    protected int leftPos, topPos;
    protected Inventory inventory;
    public ComputerExploreScreen(ComputerMenu container, Inventory inventory, Component text) {
        super(text);
        this.menu = container;
        this.inventory = inventory;
    }

    @Override
    public ComputerMenu getMenu() {
        return menu;
    }

    protected List<GuiEventListener> foldersAndFiles = new ArrayList<>();

    protected void createElements() {
        foldersAndFiles.forEach(this::removeWidget);
        foldersAndFiles.clear();

        int x = this.leftPos;
        int y = this.topPos;
        AtomicInteger yOffset = new AtomicInteger(0);
        menu.getData().getFolderSafe(menu.getWorkingDir()).ifPresent(cwd -> {
            if (!menu.getWorkingDir().toString().isEmpty())
                foldersAndFiles.add(this.addRenderableWidget(new Button(x, y + yOffset.getAndAdd(23), 200, 20, new TextComponent(".."), (self) -> {
                    menu.setWorkingDir(menu.getWorkingDir().getParent());
                    createElements();
                })));
            cwd.folders.forEach((name, folder) -> {
                foldersAndFiles.add(this.addRenderableWidget(new Button(x, y + yOffset.getAndAdd(23), 200, 20, new TextComponent("Open Text Doc"), (self) -> {
                    menu.setWorkingDir(menu.getWorkingDir().getParent());
                    createElements();
                })));
            });
            cwd.files.forEach((name, folder) -> {
                foldersAndFiles.add(this.addRenderableWidget(new Button(x, y + yOffset.getAndAdd(23), 200, 20, new TextComponent("Open Text Doc"), (self) -> {
                    this.openFile(menu.getWorkingDir().resolve(name));
                })));
            });
        });
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        createElements();
    }

    public ResourceLocation getBackground() {
        return Changed.modResource("textures/gui/computer/desktop.png");
    }

    @Override
    public void render(PoseStack pose, int cursorX, int cursorY, float partialTicks) {
        this.renderBackground(pose);
        RenderSystem.setShaderTexture(0, getBackground());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        super.render(pose, cursorX, cursorY, partialTicks);
    }

    public void openFile(Path fullPath) {
        File file = this.menu.getData().getFile(fullPath);
        if (file == null) // 404
            return;

        switch (file.type) {
            case PICTURE -> {} // TODO open window with picture
            case TEXT -> this.minecraft.setScreen(new ComputerTextScreen(this, this.menu, this.inventory, ComputerTextScreen.TITLE));
            case RECIPE -> this.menu.setDirty(this.menu.requestRecipe(fullPath));
            default -> {} // No action
        }
    }
}