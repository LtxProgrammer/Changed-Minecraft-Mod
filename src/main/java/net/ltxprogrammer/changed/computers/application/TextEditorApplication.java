package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.LexicalPath;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.network.ChangedPackets;
import net.ltxprogrammer.changed.network.packet.ComputerAppSyncPacket;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TextEditorApplication implements Application {
    protected final ComputerMenu menu;
    protected @Nullable LexicalPath.Absolute activeFile;
    protected String fileContent = "";

    public TextEditorApplication(ComputerMenu menu, List<String> args) {
        this.menu = menu;

        if (!args.isEmpty()) {
            this.openFile(LexicalPath.of(args.get(0)));
        } else
            this.activeFile = null;
    }

    public void openFile(LexicalPath relativePath) {
        this.activeFile = menu.getWorkingDir().resolve(relativePath);
        var f = menu.computer.getFileOfType(this.activeFile, File.Type.TEXT);

        f.ifLeft(file -> {
            fileContent = file.getContent();
        }).ifRight(error -> {
            fileContent = "Could not open \"" + relativePath + "\": " + error;
        });
    }

    public void saveAs(LexicalPath relativePath) {
        this.activeFile = menu.getWorkingDir().resolve(relativePath);
        this.save();
    }

    public void save() {
        if (this.activeFile == null)
            return; // Force prompt for save location?

        if (menu.getPlayer().level().isClientSide()) {
            CompoundTag payload = new CompoundTag();
            payload.putString("op", "save");
            payload.putString("path", this.activeFile.toString());
            payload.putString("content", this.fileContent);
            Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
        } else {
            var f = menu.computer.getOrCreateFile(this.activeFile, File.Type.TEXT);

            f.ifLeft(file -> {
                file.setContent(this.fileContent);
            });

            CompoundTag payload = new CompoundTag();
            payload.putString("op", "save");
            payload.putString("path", this.activeFile.toString());
            payload.putString("content", this.fileContent);
            Changed.PACKET_HANDLER.send(ChangedPackets.TRACKING_BLOCK_ENTITY.with(() -> menu.computer),
                    ComputerAppSyncPacket.syncApplication(this.getType(), payload));
        }
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String content) {
        this.fileContent = content;
    }

    public @Nullable LexicalPath.Absolute getActiveFile() {
        return activeFile;
    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.TEXT_EDITOR.get();
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        String op = payload.getString("op");

        if ("save".equals(op)) {
            this.activeFile = LexicalPath.of(payload.getString("path")).assertAbsolute();
            this.fileContent = payload.getString("content");

            if (receiver.isServer()) {
                this.save();
            } else {
                var f = menu.computer.getOrCreateFile(this.activeFile, File.Type.TEXT);

                f.ifLeft(file -> {
                    file.setContent(this.fileContent);
                });
            }
        }
    }
}
