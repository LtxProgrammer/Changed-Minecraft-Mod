package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.LexicalPath;
import net.ltxprogrammer.changed.computers.application.ISaveDialogListener;
import net.ltxprogrammer.changed.computers.application.SaveFileDialog;
import net.ltxprogrammer.changed.network.packet.ComputerAppSyncPacket;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class SaveFileDialogScreen extends FileExplorerScreen {
    protected final SaveFileDialog application;
    protected EditBox lastEditBox = null;

    public SaveFileDialogScreen(SaveFileDialog application, ComputerScreen screen) {
        super(application, screen);
        this.application = application;
    }

    protected void saveFile(LexicalPath.Absolute folderPath, String name) {
        CompoundTag payload = new CompoundTag();
        payload.putString("control", "save");
        payload.putString("path", folderPath.toString());
        payload.putString("name", name);
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(application.getType(), payload));

        if (screen.getMenu().previousApplication() instanceof ISaveDialogListener listener)
            listener.acceptSavePath(folderPath.resolve(name));
        appCloser.run();
    }

    @Override
    protected void openFile(LexicalPath.Absolute fullPath) {
        LexicalPath.Absolute folderPath = fullPath.getParent();
        String fileName = fullPath.getFileName().toString();

        // Save instead of opening
        this.saveFile(folderPath, fileName);
    }

    @Override
    protected int getScrollRegionHeight() {
        return super.getScrollRegionHeight() - 23;
    }

    @Override
    protected int getScrollElementCount() {
        return super.getScrollElementCount() - 1;
    }

    @Override
    protected int buildRegularListings(boolean isLocal) {
        int yOffset = super.buildRegularListings(isLocal);

        int x = desktopLeft + 4;
        int y = desktopTop + desktopHeight - 4 - 23 - 5;

        int startingWidth = desktopWidth - 14;

        ComputerMenu menu = screen.getMenu();

        var saveButton = screen.addApplicationWidget(Button.builder(COMPONENT_SAVE, (self) -> {
                    if (lastEditBox != null)
                        this.openFile(menu.getWorkingDir().resolve(lastEditBox.getValue())); // Trigger save
                }).bounds(x + startingWidth - 20, y, 20, 20)
                .tooltip(Tooltip.create(COMPONENT_SAVE))
                .build(ApplicationScreen.iconButton2(screen::getTheme, 0, 0)));
        startingWidth -= 23;

        var saveAsWidget = screen.addApplicationWidget(new EditBox(screen.getMinecraft().font, x, y, startingWidth, 20, Component.empty()) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int mods) {
                if (!this.canConsumeInput()) {
                    return false;
                } else {
                    if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                        String value = this.getValue();
                        SaveFileDialogScreen.this.openFile(menu.getWorkingDir().resolve(value)); // Trigger save
                        return true;
                    }
                }
                return super.keyPressed(keyCode, scanCode, mods);
            }
        });
        saveAsWidget.setResponder(text -> {
            saveButton.visible = LexicalPath.isFileNameValid(text);
        });
        saveAsWidget.setFilter(LexicalPath::isFileNameValidOrIsEmpty);
        saveAsWidget.setValue(lastEditBox == null ? application.defaultName : lastEditBox.getValue());

        lastEditBox = saveAsWidget;

        return yOffset;
    }
}
