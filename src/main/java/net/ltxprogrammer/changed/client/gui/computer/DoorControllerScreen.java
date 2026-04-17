package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.DoorControllerApplication;
import net.ltxprogrammer.changed.computers.application.FileExplorerApplication;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class DoorControllerScreen implements ApplicationScreen {
    public static final ResourceLocation ICON_ATLAS = Changed.modResource("file_explorer_icons");

    static Function<Button.Builder, Button> explorerListItemButton(Supplier<UITheme> themeSupplier, int iconX, int iconY) {
        return ApplicationScreen.listItemButton(themeSupplier, ICON_ATLAS, iconX, iconY, 1, 2, 16, 16, 64, 96, 32);
    }

    protected final DoorControllerApplication application;
    protected final ComputerScreen screen;

    protected final SingleRunnable appCloser;

    protected @Nullable Integer selectedDoor;
    protected StringWidget doorNameWidget;
    protected Button openDoorButton;
    protected Button closeDoorButton;
    protected Checkbox automaticCheckbox;

    public DoorControllerScreen(DoorControllerApplication application, ComputerScreen screen) {
        this.application = application;
        this.screen = screen;

        this.appCloser = new SingleRunnable(() -> {
            Changed.PACKET_HANDLER.sendToServer(
                    ComputerAppClosePacket.closeApplication(application.getType()));
        });
    }

    protected void updateDoorActionButtons(Integer door) {
        if (doorNameWidget == null)
            return;

        selectedDoor = door;

        this.doorNameWidget.visible = door != null;
        this.openDoorButton.visible = door != null;
        this.closeDoorButton.visible = door != null;
        this.automaticCheckbox.visible = door != null;

        if (door == null)
            return;

        var level = screen.getMinecraft().level;
        var info = application.reachableDevices.get(door);
        if (info == null) {
            this.doorNameWidget.visible = false;
            this.openDoorButton.visible = false;
            this.closeDoorButton.visible = false;
            this.automaticCheckbox.visible = false;
            return;
        }

        this.doorNameWidget.setMessage(info.deviceName());

        var blockState = level == null ? null : level.getBlockState(info.position());
        if (blockState != null && blockState.hasProperty(BlockStateProperties.OPEN)) {
            this.openDoorButton.active = !blockState.getValue(BlockStateProperties.OPEN);
            this.closeDoorButton.active = !this.openDoorButton.active;
        } else {
            this.openDoorButton.active = true;
            this.closeDoorButton.active = true;
        }
    }

    @Override
    public void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        screen.clearApplicationWidgets();

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(0);

        ComputerMenu menu = screen.getMenu();

        screen.addApplicationWidget(Button.builder(Component.literal("Desktop"), (self) -> {
            appCloser.run();
        }).bounds(x, y + yOffset.getAndAdd(23), 20, 20)
                .tooltip(Tooltip.create(Component.literal("Exit")))
                .build(ApplicationScreen.iconButton(screen::getTheme, 200, 0)));

        application.reachableDevices.forEach((logicalDevice, info) -> {
            screen.addApplicationWidget(Button.builder(info.deviceName(), (self) -> {
                this.updateDoorActionButtons(logicalDevice);
            }).bounds(x, y + yOffset.getAndAdd(23), 100, 20)
                    .tooltip(Tooltip.create(info.deviceName()))
                    .build());
        });

        this.doorNameWidget = screen.addApplicationWidget(new StringWidget(x + 103, y + 23, 206, 20, Component.empty(), screen.getMinecraft().font))
                .alignCenter();

        this.openDoorButton = screen.addApplicationWidget(Button.builder(Component.literal("Open Door"), (self) -> {
            if (this.selectedDoor != null)
                application.requestCommand(DoorControllerApplication.Command.OPEN_DOOR, this.selectedDoor);

            this.updateDoorActionButtons(this.selectedDoor);
            if (this.automaticCheckbox != null)
                this.automaticCheckbox.selected = false;
        }).bounds(x + 103, y + 46, 100, 20)
                .build());

        this.closeDoorButton = screen.addApplicationWidget(Button.builder(Component.literal("Close Door"), (self) -> {
            if (this.selectedDoor != null)
                application.requestCommand(DoorControllerApplication.Command.CLOSE_DOOR, this.selectedDoor);

            this.updateDoorActionButtons(this.selectedDoor);
            if (this.automaticCheckbox != null)
                this.automaticCheckbox.selected = false;
        }).bounds(x + 206, y + 46, 100, 20)
                .build());

        this.automaticCheckbox = screen.addApplicationWidget(new Checkbox(x + 103, y + 69, 2003, 20, Component.literal("Automatic"), true) {
            @Override
            public void onPress() {
                super.onPress();
                if (DoorControllerScreen.this.selectedDoor != null)
                    application.requestCommand(this.selected() ?
                            DoorControllerApplication.Command.AUTOMATIC :
                            DoorControllerApplication.Command.MANUAL, DoorControllerScreen.this.selectedDoor);

                DoorControllerScreen.this.updateDoorActionButtons(DoorControllerScreen.this.selectedDoor);
            }
        });
    }

    @Override
    public void tick(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        if (application.devicesDirty) {
            application.devicesDirty = false;
            initialize(desktopLeft, desktopTop, desktopWidth, desktopHeight);
        }

        this.updateDoorActionButtons(this.selectedDoor);
    }
}
