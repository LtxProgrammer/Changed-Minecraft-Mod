package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.FileExplorerApplication;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class FileExplorerScreen implements ApplicationScreen {
    public static final ResourceLocation BACKGROUND = Changed.modResource("textures/gui/computer/app_bg/file_explorer.png");
    public static final ResourceLocation ICON_ATLAS = Changed.modResource("file_explorer_icons");

    static Function<Button.Builder, Button> explorerListItemButton(Supplier<UITheme> themeSupplier, int iconX, int iconY) {
        return ApplicationScreen.listItemButtonThemed(themeSupplier, ICON_ATLAS, iconX, iconY, 2, 2, 16, 16, 64, 96, 32);
    }

    protected final FileExplorerApplication application;
    protected final ComputerScreen screen;

    protected final SingleRunnable appCloser;

    protected int desktopLeft;
    protected int desktopTop;
    protected int desktopWidth;
    protected int desktopHeight;

    public FileExplorerScreen(FileExplorerApplication application, ComputerScreen screen) {
        this.application = application;
        this.screen = screen;

        this.appCloser = new SingleRunnable(() -> {
            Changed.PACKET_HANDLER.sendToServer(
                    ComputerAppClosePacket.closeApplication(application.getType()));
        });
    }

    protected int buildBasicWidgets() {
        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(0);

        screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y, desktopWidth - 8, 20,
                        application.getType().getDisplayName(), screen.getMinecraft().font)
                .alignCenter().setColor(0x404040));

        screen.addApplicationWidget(Button.builder(COMPONENT_EXIT, (self) -> {
                    appCloser.run();
                }).bounds(x, y + yOffset.getAndAdd(23), 20, 20)
                .tooltip(Tooltip.create(COMPONENT_EXIT))
                .build(ApplicationScreen.iconButton(screen::getTheme, 200, 0)));

        return yOffset.getAcquire();
    }

    protected void buildRegularListings() {
        screen.clearApplicationWidgets();

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(buildBasicWidgets());

        ComputerMenu menu = screen.getMenu();

        menu.computer.getFolderSafe(menu.getWorkingDir()).ifPresent(cwd -> {
            if (menu.getWorkingDir().getParent() != null) {
                Path parentDir = menu.getWorkingDir().getParent();
                screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                            menu.setWorkingDir(parentDir);
                            buildRegularListings();
                        }).bounds(x + 23, y, 20, 20)
                        .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.parent_dir")))
                        .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));
            } else if (menu.getWorkingDir().getRoot().equals(menu.getWorkingDir())) {
                screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                            buildDriveListings();
                        }).bounds(x + 23, y, 20, 20)
                        .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.drives")))
                        .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));
            }
            cwd.folders.forEach((name, folder) -> {
                Path subDir = menu.getWorkingDir().resolve(Path.of(name + "/"));
                screen.addApplicationWidget(Button.builder(Component.literal(name + "/"), (self) -> {
                            menu.setWorkingDir(subDir);
                            buildRegularListings();
                        }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 8, 20)
                        .build(explorerListItemButton(screen::getTheme, 0, 0)));
            });
            cwd.files.forEach((name, file) -> {
                int iconX = file.type.xTexture;
                int iconY = file.type.yTexture;
                screen.addApplicationWidget(Button.builder(Component.literal(name), (self) -> {
                            screen.openFile(menu.getWorkingDir().resolve(Path.of(name)));
                        }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 8, 20)
                        .build(explorerListItemButton(screen::getTheme, iconX, iconY)));
            });
        });
    }

    protected void buildDriveListings() {
        screen.clearApplicationWidgets();

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(buildBasicWidgets());

        ComputerMenu menu = screen.getMenu();

        menu.computer.visitMountedFileSystems((driveLetter, discData) -> {
            Path subDir = Path.of(driveLetter + ":/");
            screen.addApplicationWidget(Button.builder(Component.literal(driveLetter + ":/ [" + discData.getName() + "]"), (self) -> {
                        menu.setWorkingDir(subDir);
                        buildRegularListings();
                    }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 8, 20)
                    .build(explorerListItemButton(screen::getTheme, 0, 0)));
        });
    }

    @Override
    public void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        this.desktopLeft = desktopLeft;
        this.desktopTop = desktopTop;
        this.desktopWidth = desktopWidth;
        this.desktopHeight = desktopHeight;

        buildRegularListings();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.appCloser.run();
            return true;
        }

        return ApplicationScreen.super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {
        graphics.blit(BACKGROUND, this.desktopLeft, this.desktopTop, 0, 0,
                this.desktopWidth, this.desktopHeight, this.desktopWidth, this.desktopHeight);
    }
}
