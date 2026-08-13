package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.LexicalPath;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.FileExplorerApplication;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.network.packet.ComputerAppSyncPacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class FileExplorerScreen implements ApplicationScreen {
    public static final ResourceLocation BACKGROUND = Changed.modResource("textures/gui/computer/app_bg/file_explorer.png");
    public static final ResourceLocation ICON_ATLAS = Changed.modResource("file_explorer_icons");
    private static final int SCROLL_BUFFER = 3;

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

    protected ScrollBarVerticalStepped scrollBar;
    protected StringWidget bottomText;

    protected boolean listenForDeviceUpdates = false;
    protected Runnable refreshListings = () -> buildRegularListings(true);

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

        this.scrollBar = screen.addApplicationWidget(this.scrollBar != null ? this.scrollBar : ApplicationScreen.verticalScrollBarStepped(screen::getTheme, desktopLeft + 314, desktopTop + 27, 6, 163)
                .setCanvasSize(5).setViewportSize(7).setScrollListener((lastScroll, scroll) -> {
            if (lastScroll == scroll)
                return;
            refreshListings.run();
            screen.setFocused(this.scrollBar);
        }));

        this.bottomText = screen.addApplicationWidget(this.bottomText != null ? this.bottomText : ApplicationScreen.shadowlessString(x, desktopTop + 191, desktopWidth, 9,
                        Component.empty(), screen.getMinecraft().font)
                .alignLeft().setColor(0x404040));

        return yOffset.getAcquire();
    }

    protected void buildRegularListings(boolean isLocal) {
        screen.clearApplicationWidgets();

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(buildBasicWidgets());

        ComputerMenu menu = screen.getMenu();

        if (isLocal) {
            screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                        buildNetworkListings(false);
                    }).bounds(x + 46, y, 20, 20)
                    .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.view_network")))
                    .build(ApplicationScreen.iconButton2(screen::getTheme, 40, 0)));
        } else {
            screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                        CompoundTag payload = new CompoundTag();
                        payload.putString("control", "unmount");
                        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(application.getType(), payload));

                        buildDriveListings();
                    }).bounds(x + 46, y, 20, 20)
                    .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.local")))
                    .build(ApplicationScreen.iconButton2(screen::getTheme, 60, 0)));
        }

        AtomicInteger elementCount = new AtomicInteger(0);
        var workingFolder = menu.computer.getFolderSafe(menu.getWorkingDir());
        workingFolder.ifPresent(cwd -> {
            if (menu.getWorkingDir().getParent() != null) {
                var parentDir = menu.getWorkingDir().getParent();
                screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                            menu.setWorkingDir(parentDir);
                            buildRegularListings(isLocal);
                        }).bounds(x + 23, y, 20, 20)
                        .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.parent_dir")))
                        .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));
            } else if (isLocal && menu.getWorkingDir().getRoot().equals(menu.getWorkingDir())) {
                screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                            buildDriveListings();
                        }).bounds(x + 23, y, 20, 20)
                        .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.view_drives")))
                        .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));
            }
            cwd.folders.forEach((name, folder) -> {
                int elementIndex = elementCount.getAndIncrement();
                if (elementIndex < scrollBar.getScroll() || elementIndex >= scrollBar.getScrollNext())
                    return;
                var subDir = menu.getWorkingDir().resolve(LexicalPath.of(name + "/"));
                screen.addApplicationWidget(Button.builder(Component.literal(name + "/"), (self) -> {
                            menu.setWorkingDir(subDir);
                            buildRegularListings(isLocal);
                        }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 14, 20)
                        .build(explorerListItemButton(screen::getTheme, File.Type.FOLDER.xTexture, File.Type.FOLDER.yTexture)));
            });
            cwd.files.forEach((name, file) -> {
                int elementIndex = elementCount.getAndIncrement();
                if (elementIndex < scrollBar.getScroll() || elementIndex >= scrollBar.getScrollNext())
                    return;
                int iconX = file.type.xTexture;
                int iconY = file.type.yTexture;
                screen.addApplicationWidget(Button.builder(Component.literal(name), (self) -> {
                            screen.openFile(menu.getWorkingDir().resolve(LexicalPath.of(name)));
                        }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 14, 20)
                        .build(explorerListItemButton(screen::getTheme, iconX, iconY)));
            });

            if (elementCount.getAcquire() <= 0) {
                screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y + yOffset.getAndAdd(23), desktopWidth, 20,
                                Component.translatable("application.changed.file_explorer.empty_folder"), screen.getMinecraft().font)
                        .alignCenter().setColor(0x404040));
            }
        });

        if (workingFolder.isEmpty()) {
            screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                        buildDriveListings();
                    }).bounds(x + 23, y, 20, 20)
                    .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.view_drives")))
                    .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));

            int elementIndex = elementCount.getAndIncrement();
            if (!(elementIndex < scrollBar.getScroll() || elementIndex >= scrollBar.getScrollNext())) {
                screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y + yOffset.getAndAdd(23), desktopWidth, 20,
                                Component.translatable("application.changed.file_explorer.invalid_folder", menu.getWorkingDir().toString()), screen.getMinecraft().font)
                        .alignCenter().setColor(0x404040));
            }
        }

        this.bottomText.setMessage(
                Component.translatable("application.changed.file_explorer.item_count", elementCount.getAcquire())
                        .append(" | ")
                        .append(menu.getWorkingDir().toString())
        );

        scrollBar.setCanvasSize(elementCount.getAcquire() + SCROLL_BUFFER);
        application.listingsDirty = false;
        listenForDeviceUpdates = false;
        refreshListings = () -> buildRegularListings(isLocal);
    }

    protected void buildDriveListings() {
        screen.clearApplicationWidgets();

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(buildBasicWidgets());

        ComputerMenu menu = screen.getMenu();

        screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                    buildNetworkListings(true);
                }).bounds(x + 46, y, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.view_network")))
                .build(ApplicationScreen.iconButton2(screen::getTheme, 40, 0)));

        AtomicInteger elementCount = new AtomicInteger(0);
        menu.computer.visitMountedFileSystems((driveLetter, discData, ejectable) -> {
            int elementIndex = elementCount.getAndIncrement();
            if (elementIndex < scrollBar.getScroll() || elementIndex >= scrollBar.getScrollNext())
                return;

            var subDir = LexicalPath.fromDriveLetter(driveLetter);
            int buttonWidth = desktopWidth - 14;
            if (ejectable) {
                screen.addApplicationWidget(Button.builder(Component.literal("Eject"), (self) -> {
                            CompoundTag payload = new CompoundTag();
                            payload.putString("control", "eject");
                            payload.putString("letter", String.valueOf(driveLetter));
                            Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(application.getType(), payload));

                            self.active = false;
                        }).bounds(x + (buttonWidth - 20), y + yOffset.get(), 20, 20)
                        .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.eject_drive")))
                        .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));
                buttonWidth -= 23;
            }

            screen.addApplicationWidget(Button.builder(Component.literal(driveLetter + ":/ [" + discData.getName() + "]"), (self) -> {
                        menu.setWorkingDir(subDir);
                        buildRegularListings(true);
                    }).bounds(x, y + yOffset.getAndAdd(23), buttonWidth, 20)
                    .build(explorerListItemButton(screen::getTheme, File.Type.FOLDER.xTexture, File.Type.FOLDER.yTexture)));
        });

        this.bottomText.setMessage(
                Component.translatable("application.changed.file_explorer.drives")
        );

        scrollBar.setCanvasSize(elementCount.getAcquire() + SCROLL_BUFFER);
        application.listingsDirty = false;
        listenForDeviceUpdates = false;
        refreshListings = this::buildDriveListings;
    }

    protected void buildNetworkListings(boolean returnToDrives) {
        screen.clearApplicationWidgets();

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(buildBasicWidgets());

        ComputerMenu menu = screen.getMenu();
        screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                    if (returnToDrives)
                        buildDriveListings();
                    else
                        buildRegularListings(true);
                }).bounds(x + 46, y, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.local")))
                .build(ApplicationScreen.iconButton2(screen::getTheme, 60, 0)));

        AtomicInteger elementCount = new AtomicInteger(0);
        application.reachableDevices.forEach((logicalAddress, deviceInfo) -> {
            int elementIndex = elementCount.getAndIncrement();
            if (elementIndex < scrollBar.getScroll() || elementIndex >= scrollBar.getScrollNext())
                return;

            screen.addApplicationWidget(Button.builder(deviceInfo.deviceName(), (self) -> {
                        CompoundTag payload = new CompoundTag();
                        payload.putString("control", "mount");
                        payload.putInt("address", logicalAddress);
                        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(application.getType(), payload));

                        self.active = false;
                        self.setMessage(Component.literal("Reading..."));
                    }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 14, 20)
                    .build(explorerListItemButton(screen::getTheme, File.Type.FOLDER.xTexture, File.Type.FOLDER.yTexture)));
        });

        if (application.reachableDevices.isEmpty()) {
            int elementIndex = elementCount.getAndIncrement();
            if (!(elementIndex < scrollBar.getScroll() || elementIndex >= scrollBar.getScrollNext())) {
                screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y + yOffset.getAndAdd(23), desktopWidth, 20,
                                Component.translatable("application.changed.file_explorer.empty_network"), screen.getMinecraft().font)
                        .alignCenter().setColor(0x404040));
            }
        }

        this.bottomText.setMessage(
                Component.translatable("application.changed.file_explorer.network")
        );

        scrollBar.setCanvasSize(elementCount.getAcquire() + SCROLL_BUFFER);
        application.devicesDirty = false;
        application.listingsDirty = false;
        listenForDeviceUpdates = true;
        refreshListings = () -> buildNetworkListings(returnToDrives);
    }

    @Override
    public void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        this.desktopLeft = desktopLeft;
        this.desktopTop = desktopTop;
        this.desktopWidth = desktopWidth;
        this.desktopHeight = desktopHeight;

        refreshListings.run();
    }

    @Override
    public void tick(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        ApplicationScreen.super.tick(desktopLeft, desktopTop, desktopWidth, desktopHeight);
        if (application.openDriveLetter != null) {
            screen.getMenu().setWorkingDir(LexicalPath.fromDriveLetter(application.openDriveLetter));
            buildRegularListings(false);

            application.openDriveLetter = null;
        }

        if (application.listingsDirty) {
            refreshListings.run();

            application.listingsDirty = false;
        }

        if (application.devicesDirty) {
            if (listenForDeviceUpdates)
                refreshListings.run();

            application.devicesDirty = false;
        }
    }

    protected boolean isMouseInElementArea(double x, double y) {
        int textBoxLeft = desktopLeft;
        int textBoxWidth = desktopWidth - 6;
        int textBoxTop = desktopTop + 27;
        int textBoxHeight = 163;

        return x > textBoxLeft && x < (textBoxLeft + textBoxWidth) && y > textBoxTop && y < (textBoxTop + textBoxHeight);
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
    public boolean mouseScrolled(double x, double y, double yOffset) {
        if (isMouseInElementArea(x, y) && this.scrollBar.mouseScrolled(x, y, yOffset))
            return true;

        return ApplicationScreen.super.mouseScrolled(x, y, yOffset);
    }

    @Override
    public void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {
        graphics.blit(BACKGROUND, this.desktopLeft, this.desktopTop, 0, 0,
                this.desktopWidth, this.desktopHeight, this.desktopWidth, this.desktopHeight);
    }
}
