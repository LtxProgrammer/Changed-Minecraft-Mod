package net.ltxprogrammer.changed.client.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ComputerExploreScreen extends Screen implements MenuAccess<ComputerMenu> {
    public enum Mode {
        DESKTOP,
        FILE_EXPLORER
    }

    protected final ComputerMenu menu;
    protected int imageWidth, imageHeight;
    protected int leftPos, topPos;
    protected Inventory inventory;

    protected Mode constructedMode;

    public ComputerExploreScreen(ComputerMenu container, Inventory inventory, Component text) {
        super(text);
        this.menu = container;
        this.inventory = inventory;
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

    protected void createDesktop(Consumer<AbstractWidget> widgetConsumer) {
        int widthInsideMargin = this.imageWidth - 12 - 8;
        int iconsPerRow = 8;

        int leftMargin = this.leftPos + 6 + 4;
        int topMargin = this.topPos + 6 + 4;
        int buttonWidth = 32;
        int buttonHeight = 32;
        float buttonHeightSpacing = buttonHeight + 3.75f;
        float buttonWidthSpacing = buttonWidth + ((float)widthInsideMargin / iconsPerRow - buttonWidth);
        //int buttonWidthLong = buttonWidth + buttonWidthSpacing;

        final AtomicInteger widgetIndex = new AtomicInteger(0);

        {
            int index = widgetIndex.getAndIncrement();
            widgetConsumer.accept(Button.builder(Component.literal("Explorer"), (self) -> {
                menu.setWorkingDir(menu.getHomeDir());
                createElements(Mode.FILE_EXPLORER);
            }).bounds(leftMargin + (int) (buttonWidthSpacing * (index % 9)),
                    topMargin + (int) (buttonHeightSpacing * (int) (index / 9)),
                    buttonWidth,
                    buttonHeight).build(builder -> new Button(builder) {
                @Override
                protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTicks) {
                    super.renderWidget(graphics, mx, my, partialTicks);
                }
            }));
        }

        {
            int index = widgetIndex.getAndIncrement();
            widgetConsumer.accept(Button.builder(Component.literal("Stasis Chamber"), (self) -> {

            }).bounds(leftMargin + (int) (buttonWidthSpacing * (index % 9)),
                    topMargin + (int) (buttonHeightSpacing * (int) (index / 9)),
                    buttonWidth,
                    buttonHeight).build());
        }

        {
            int index = widgetIndex.getAndIncrement();
            widgetConsumer.accept(Button.builder(Component.literal("Door"), (self) -> {

            }).bounds(leftMargin + (int) (buttonWidthSpacing * (index % 9)),
                    topMargin + (int) (buttonHeightSpacing * (int) (index / 9)),
                    buttonWidth,
                    buttonHeight).build());
        }
    }

    protected void createFileExplorer(Consumer<AbstractWidget> widgetConsumer) {
        int x = this.leftPos + 6 + 4;
        int y = this.topPos + 6 + 4;
        AtomicInteger yOffset = new AtomicInteger(0);

        widgetConsumer.accept(Button.builder(Component.literal("Desktop"), (self) -> {
            menu.setWorkingDir(menu.getDesktopDir());
            createElements(Mode.DESKTOP);
        }).bounds(x, y + yOffset.getAndAdd(23), 100, 20).build());

        menu.computer.getFolderSafe(menu.getWorkingDir()).ifPresent(cwd -> {
            if (menu.getWorkingDir().getParent() != null)
                widgetConsumer.accept(Button.builder(Component.literal(".."), (self) -> {
                    menu.setWorkingDir(menu.getWorkingDir().getParent());
                    createElements(Mode.FILE_EXPLORER);
                }).bounds(x, y + yOffset.getAndAdd(23), 200, 20).build());
            cwd.folders.forEach((name, folder) -> {
                widgetConsumer.accept(Button.builder(Component.literal(name + "/"), (self) -> {
                    menu.setWorkingDir(menu.getWorkingDir().resolve(Path.of(name + "/")));
                    createElements(Mode.FILE_EXPLORER);
                }).bounds(x, y + yOffset.getAndAdd(23), 200, 20).build());
            });
            cwd.files.forEach((name, file) -> {
                widgetConsumer.accept(Button.builder(Component.literal(name), (self) -> {
                    this.openFile(menu.getWorkingDir().resolve(Path.of(name)));
                }).bounds(x, y + yOffset.getAndAdd(23), 200, 20).build());
            });
        });
    }

    protected void createElements(Mode forMode) {
        widgets.forEach(this::removeWidget);
        widgets.clear();

        Consumer<AbstractWidget> widgetConsumer = widget -> widgets.add(this.addRenderableWidget(widget));

        switch (forMode) {
            case DESKTOP -> this.createDesktop(widgetConsumer);
            case FILE_EXPLORER -> this.createFileExplorer(widgetConsumer);
        }

        constructedMode = forMode;
    }

    @Override
    protected void init() {
        super.init();

        this.imageWidth = 332;
        this.imageHeight = 212;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        menu.setWorkingDir(menu.getDesktopDir());
        createElements(Mode.DESKTOP);
    }

    public ResourceLocation getBorder() {
        return Changed.modResource("textures/gui/computer/border.png");
    }

    public ResourceLocation getBackground() {
        return Changed.modResource("textures/gui/computer/bg/lines.png");
    }

    @Override
    public void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.setColor(1, 1, 1, 1);
        graphics.blit(getBorder(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        graphics.blit(getBackground(), this.leftPos+6, this.topPos+6, 0, 0,
                this.imageWidth - 12, this.imageHeight - 12, this.imageWidth - 12, this.imageHeight - 12);

        super.render(graphics, cursorX, cursorY, partialTicks);
    }

    public void openFile(Path fullPath) {
        File file = this.menu.computer.getFile(fullPath);
        if (file == null) // 404
            return;

        switch (file.type) {
            //case PICTURE -> {} // TODO open window with picture
            //case TEXT -> this.minecraft.setScreen(new ComputerTextScreen(/*this, */this.menu, this.inventory, ComputerTextScreen.TITLE));
            case RECIPE -> this.menu.setDirty(this.menu.requestRecipe(fullPath));
            default -> {} // No action
        }
    }
}