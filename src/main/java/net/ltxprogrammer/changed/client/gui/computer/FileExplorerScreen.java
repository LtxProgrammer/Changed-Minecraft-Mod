package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.FileExplorerApplication;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.network.packet.ComputerAppSyncPacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.nbt.CompoundTag;
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

    protected Integer networkDevice = null;

    protected final SingleRunnable appCloser;

    protected int desktopLeft;
    protected int desktopTop;
    protected int desktopWidth;
    protected int desktopHeight;

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
                    .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.network")))
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

        var workingFolder = menu.computer.getFolderSafe(menu.getWorkingDir());
        workingFolder.ifPresent(cwd -> {
            if (menu.getWorkingDir().getParent() != null) {
                Path parentDir = menu.getWorkingDir().getParent();
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
                        .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.drives")))
                        .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));
            }
            cwd.folders.forEach((name, folder) -> {
                Path subDir = menu.getWorkingDir().resolve(Path.of(name + "/"));
                screen.addApplicationWidget(Button.builder(Component.literal(name + "/"), (self) -> {
                            menu.setWorkingDir(subDir);
                            buildRegularListings(isLocal);
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

            if (cwd.folders.isEmpty() && cwd.files.isEmpty()) {
                screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y + yOffset.getAndAdd(23), desktopWidth - 8, 20,
                                Component.translatable("application.changed.file_explorer.empty"), screen.getMinecraft().font)
                        .alignCenter().setColor(0x404040));
            }
        });

        if (workingFolder.isEmpty()) {
            screen.addApplicationWidget(Button.builder(Component.literal(".."), (self) -> {
                        buildDriveListings();
                    }).bounds(x + 23, y, 20, 20)
                    .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.drives")))
                    .build(ApplicationScreen.iconButton(screen::getTheme, 220, 0)));

            screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y + yOffset.getAndAdd(23), desktopWidth - 8, 20,
                            Component.translatable("application.changed.file_explorer.invalid_folder", menu.getWorkingDir().toString()), screen.getMinecraft().font)
                    .alignCenter().setColor(0x404040));
        }

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
                .tooltip(Tooltip.create(Component.translatable("application.changed.file_explorer.network")))
                .build(ApplicationScreen.iconButton2(screen::getTheme, 40, 0)));

        menu.computer.visitMountedFileSystems((driveLetter, discData) -> {
            Path subDir = Path.of(driveLetter + ":/");
            screen.addApplicationWidget(Button.builder(Component.literal(driveLetter + ":/ [" + discData.getName() + "]"), (self) -> {
                        menu.setWorkingDir(subDir);
                        buildRegularListings(true);
                    }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 8, 20)
                    .build(explorerListItemButton(screen::getTheme, 0, 0)));
        });

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

        application.reachableDevices.forEach((logicalAddress, deviceInfo) -> {
            screen.addApplicationWidget(Button.builder(deviceInfo.deviceName(), (self) -> {
                        CompoundTag payload = new CompoundTag();
                        payload.putString("control", "mount");
                        payload.putInt("address", logicalAddress);
                        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(application.getType(), payload));

                        self.setMessage(Component.literal("Reading..."));
                    }).bounds(x, y + yOffset.getAndAdd(23), desktopWidth - 8, 20)
                    .build(explorerListItemButton(screen::getTheme, 0, 0)));
        });

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
            screen.getMenu().setWorkingDir(Path.of(application.openDriveLetter + ":/"));
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
