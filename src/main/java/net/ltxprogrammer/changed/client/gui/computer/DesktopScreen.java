package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.application.DesktopApplication;
import net.ltxprogrammer.changed.network.packet.ComputerAppLaunchPacket;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;

import java.util.concurrent.atomic.AtomicInteger;

public class DesktopScreen implements ApplicationScreen {
    protected final DesktopApplication application;
    protected final ComputerScreen screen;

    public DesktopScreen(DesktopApplication application, ComputerScreen screen) {
        this.application = application;
        this.screen = screen;
    }

    @Override
    public void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        screen.clearApplicationWidgets();

        int widthInsideMargin = desktopWidth - 8;
        int iconsPerRow = 8;

        int leftMargin = desktopLeft + 4;
        int topMargin = desktopTop + 4;
        int buttonWidth = 32;
        int buttonHeight = 32;
        float buttonHeightSpacing = buttonHeight + 3.75f;
        float buttonWidthSpacing = buttonWidth + ((float)widthInsideMargin / iconsPerRow - buttonWidth);

        final AtomicInteger widgetIndex = new AtomicInteger(0);

        ComputerMenu menu = screen.getMenu();

        for (var app : menu.getInstalledApplications()) {
            int index = widgetIndex.getAndIncrement();
            screen.addApplicationWidget(Button.builder(app.getDisplayName(), (self) -> {
                Changed.PACKET_HANDLER.sendToServer(ComputerAppLaunchPacket.launchApplication(app));
            }).bounds(leftMargin + (int) (buttonWidthSpacing * (index % 9)),
                    topMargin + (int) (buttonHeightSpacing * (int) (index / 9)),
                    buttonWidth,
                    buttonHeight)
            .tooltip(Tooltip.create(app.getDisplayName()))
            .build(ApplicationScreen.iconButton(screen::getTheme, app.getIconLocation(),
                    0, 0, 0, 0, 32, 32, 32, 96, 32)));
        }
    }
}
