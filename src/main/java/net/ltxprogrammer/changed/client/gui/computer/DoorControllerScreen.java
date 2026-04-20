package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.DoorControllerApplication;
import net.ltxprogrammer.changed.computers.protocol.LogicalNetworkInterface;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class DoorControllerScreen implements ApplicationScreen {
    public static final ResourceLocation ICON_ATLAS = Changed.modResource("file_explorer_icons");

    public static ResourceLocation getDeviceIconLocation(ResourceLocation icon) {
        return ResourceLocation.fromNamespaceAndPath(
                icon.getNamespace(),
                "textures/gui/computer/devices/%s.png".formatted(icon.getPath())
        );
    }

    static Function<Button.Builder, Button> deviceListItemButton(Supplier<UITheme> themeSupplier, ResourceLocation deviceIcon) {
        return ApplicationScreen.listItemButtonStatic(themeSupplier,
                getDeviceIconLocation(deviceIcon), 0, 0, 1, 2, 16, 16, 16, 16, 16);
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
        BlockPos computerPos = menu.computer.getBlockPos();

        screen.addApplicationWidget(Button.builder(Component.literal("Desktop"), (self) -> {
            appCloser.run();
        }).bounds(x, y + yOffset.getAndAdd(23), 20, 20)
                .tooltip(Tooltip.create(Component.literal("Exit")))
                .build(ApplicationScreen.iconButton(screen::getTheme, 200, 0)));

        application.reachableDevices.entrySet().stream().sorted(
                Comparator.comparingInt(left -> left.getValue().position().distManhattan(computerPos))
        ).forEach(entry -> {
            final var logicalDevice = entry.getKey();
            final var info = entry.getValue();
            screen.addApplicationWidget(Button.builder(info.deviceName(), (self) -> {
                        this.updateDoorActionButtons(logicalDevice);
                    }).bounds(x, y + yOffset.getAndAdd(23), 136, 20)
                    .tooltip(Tooltip.create(info.deviceName()))
                    .build(deviceListItemButton(screen::getTheme, info.icon())));
        });

        int operationsPaneX = x + 139;
        int operationsPaneY = y + 23;
        int operationsPaneWidth = desktopWidth - 136 - 3 - 8;
        int operationsPaneHalfWidth = (operationsPaneWidth / 2) - 2;

        this.doorNameWidget = screen.addApplicationWidget(new StringWidget(operationsPaneX, operationsPaneY, operationsPaneWidth, 20, Component.empty(), screen.getMinecraft().font))
                .alignCenter();

        this.openDoorButton = screen.addApplicationWidget(Button.builder(Component.literal("Open Door"), (self) -> {
            if (this.selectedDoor != null)
                application.requestCommand(DoorControllerApplication.Command.OPEN_DOOR, this.selectedDoor);

            this.updateDoorActionButtons(this.selectedDoor);
            if (this.automaticCheckbox != null)
                this.automaticCheckbox.selected = false;
        }).bounds(operationsPaneX, operationsPaneY + 23, operationsPaneHalfWidth, 20)
                .build(ApplicationScreen.textButton(screen::getTheme)));

        this.closeDoorButton = screen.addApplicationWidget(Button.builder(Component.literal("Close Door"), (self) -> {
            if (this.selectedDoor != null)
                application.requestCommand(DoorControllerApplication.Command.CLOSE_DOOR, this.selectedDoor);

            this.updateDoorActionButtons(this.selectedDoor);
            if (this.automaticCheckbox != null)
                this.automaticCheckbox.selected = false;
        }).bounds(operationsPaneX + operationsPaneHalfWidth + 3, operationsPaneY + 23, operationsPaneHalfWidth, 20)
                .build(ApplicationScreen.textButton(screen::getTheme)));

        this.automaticCheckbox = screen.addApplicationWidget(ApplicationScreen.checkBox(screen::getTheme, operationsPaneX, operationsPaneY + 46, operationsPaneWidth, 20, Component.literal("Automatic"), true, self -> {
            if (this.selectedDoor != null)
                application.requestCommand(self.selected() ?
                        DoorControllerApplication.Command.AUTOMATIC :
                        DoorControllerApplication.Command.MANUAL, this.selectedDoor);

            this.updateDoorActionButtons(this.selectedDoor);
        }));
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
