package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.ExoskeletonControllerApplication;
import net.ltxprogrammer.changed.computers.protocol.NetworkInterface;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExoControllerScreen implements ApplicationScreen {
    public static final ResourceLocation BACKGROUND = Changed.modResource("textures/gui/computer/app_bg/door_controller.png");

    public static ResourceLocation getDeviceIconLocation(ResourceLocation icon) {
        return ResourceLocation.fromNamespaceAndPath(
                icon.getNamespace(),
                "textures/gui/computer/devices/%s.png".formatted(icon.getPath())
        );
    }

    static Function<Button.Builder, Button> deviceListItemButton(Supplier<UITheme> themeSupplier, ResourceLocation deviceIcon) {
        return ApplicationScreen.listItemButtonStatic(themeSupplier,
                getDeviceIconLocation(deviceIcon), 0, 0, 2, 2, 16, 16, 16, 16, 16);
    }

    protected final ExoskeletonControllerApplication application;
    protected final ComputerScreen screen;

    protected final SingleRunnable appCloser;

    protected @Nullable NetworkInterface.Address selectedExo;
    protected EditBox exoNameWidget;
    protected Checkbox enabledCheckbox;
    protected Checkbox hostileCheckbox;

    protected int desktopLeft;
    protected int desktopTop;
    protected int desktopWidth;
    protected int desktopHeight;

    public ExoControllerScreen(ExoskeletonControllerApplication application, ComputerScreen screen) {
        this.application = application;
        this.screen = screen;

        this.appCloser = new SingleRunnable(() -> {
            Changed.PACKET_HANDLER.sendToServer(
                    ComputerAppClosePacket.closeApplication(application.getType()));
        });
    }

    protected void updateExoActionButtons(NetworkInterface.Address exo) {
        if (exoNameWidget == null)
            return;

        boolean exoChanged = exo != selectedExo;
        selectedExo = exo;

        this.exoNameWidget.visible = exo != null;
        this.enabledCheckbox.visible = exo != null;
        this.hostileCheckbox.visible = exo != null;

        if (exo == null)
            return;

        var level = screen.getMinecraft().level;
        var info = application.reachableDevices.get(exo);
        if (info == null) {
            this.exoNameWidget.visible = false;
            this.enabledCheckbox.visible = false;
            this.hostileCheckbox.visible = false;
            return;
        }

        var doorName = info.deviceName().getString();
        if (!this.exoNameWidget.isFocused()) {
            this.exoNameWidget.setResponder(null);
            this.exoNameWidget.setValue(doorName);
            this.exoNameWidget.setResponder(text -> {
                if (text.equals(doorName)) {
                    // Revert name to translatable component
                } else {
                    // Set name to literal component
                }
            });
        }

        var entity = level == null ? null : exo.getEntity(level);
        if (entity instanceof Exoskeleton exoskeleton) {
            this.enabledCheckbox.active = true;
            this.hostileCheckbox.active = true;

            if (exoChanged) {
                this.enabledCheckbox.selected = exoskeleton.isEnabled();
                this.hostileCheckbox.selected = exoskeleton.isHostile();
            }
        } else {
            this.enabledCheckbox.active = false;
            this.hostileCheckbox.active = false;
        }
    }

    @Override
    public void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        screen.clearApplicationWidgets();

        this.desktopLeft = desktopLeft;
        this.desktopTop = desktopTop;
        this.desktopWidth = desktopWidth;
        this.desktopHeight = desktopHeight;

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(0);

        ComputerMenu menu = screen.getMenu();
        BlockPos computerPos = menu.computer.getBlockPos();

        screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y, desktopWidth - 8, 20,
                application.getType().getDisplayName(), screen.getMinecraft().font)
                .alignCenter().setColor(0x404040));

        screen.addApplicationWidget(Button.builder(COMPONENT_EXIT, (self) -> {
            appCloser.run();
        }).bounds(x, y + yOffset.getAndAdd(23), 20, 20)
                .tooltip(Tooltip.create(COMPONENT_EXIT))
                .build(ApplicationScreen.iconButton(screen::getTheme, 200, 0)));

        application.reachableDevices.entrySet().stream().sorted(
                Comparator.comparingInt(left -> left.getValue().position().distManhattan(computerPos))
        ).forEach(entry -> {
            final var logicalDevice = entry.getKey();
            final var info = entry.getValue();
            screen.addApplicationWidget(Button.builder(info.deviceName(), (self) -> {
                        this.updateExoActionButtons(logicalDevice);
                    }).bounds(x, y + yOffset.getAndAdd(23), 136, 20)
                    .tooltip(Tooltip.create(info.deviceName()))
                    .build(deviceListItemButton(screen::getTheme, info.icon())));
        });

        int operationsPaneX = x + 141;
        int operationsPaneY = y + 23;
        int operationsPaneWidth = desktopWidth - (operationsPaneX - x) - 8;
        int operationsPaneHalfWidth = (operationsPaneWidth / 2) - 2;

        this.exoNameWidget = screen.addApplicationWidget(new EditBox(screen.getMinecraft().font, operationsPaneX, operationsPaneY, operationsPaneWidth, 20, Component.literal("Door Name")));
        this.exoNameWidget.active = false;

        this.enabledCheckbox = screen.addApplicationWidget(ApplicationScreen.checkBox(screen::getTheme, operationsPaneX, operationsPaneY + 23, operationsPaneWidth, 20, Component.translatable("application.changed.exoskeleton_controller.enabled"), true, self -> {
            if (this.selectedExo != null)
                application.requestCommand(self.selected() ?
                        ExoskeletonControllerApplication.Command.STARTUP :
                        ExoskeletonControllerApplication.Command.SHUTDOWN, this.selectedExo);

            this.updateExoActionButtons(this.selectedExo);
        }));
        this.enabledCheckbox.active = false;

        this.hostileCheckbox = screen.addApplicationWidget(ApplicationScreen.checkBox(screen::getTheme, operationsPaneX, operationsPaneY + 46, operationsPaneWidth, 20, Component.translatable("application.changed.exoskeleton_controller.hostile"), true, self -> {
            if (this.selectedExo != null)
                application.requestCommand(self.selected() ?
                        ExoskeletonControllerApplication.Command.HOSTILE :
                        ExoskeletonControllerApplication.Command.NOT_HOSTILE, this.selectedExo);

            this.updateExoActionButtons(this.selectedExo);
        }));
        this.hostileCheckbox.active = false;
    }

    @Override
    public void tick(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        if (application.devicesDirty) {
            application.devicesDirty = false;
            initialize(desktopLeft, desktopTop, desktopWidth, desktopHeight);
        }

        this.updateExoActionButtons(this.selectedExo);
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
