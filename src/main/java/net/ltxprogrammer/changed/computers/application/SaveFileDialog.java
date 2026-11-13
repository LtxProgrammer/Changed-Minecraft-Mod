package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.computers.LexicalPath;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SaveFileDialog extends FileExplorerApplication {
    public final File.Type saveAsType;
    public final String fileContent;
    public String defaultName;

    public SaveFileDialog(ComputerMenu menu, List<String> args) {
        super(menu, args);

        if (args.size() < 2)
            throw new IllegalArgumentException("args must contain a file type and file content");

        this.saveAsType = File.Type.fromSerial(args.get(0)).getOrThrow(false, message -> {});
        this.fileContent = args.get(1);

        if (args.size() >= 3)
            this.defaultName = args.get(2);
    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.SAVE_FILE_DIALOG.get();
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver.isServer()) {
            String control = payload.getString("control");
            if ("save".equals(control)) {
                LexicalPath.Absolute path = LexicalPath.of(payload.getString("path")).assertAbsolute();
                String name = payload.getString("name");
                Folder folder = menu.computer.getFolder(path);
                if (folder == null)
                    return;

                folder.addFile(name, new File(this.saveAsType, this.fileContent, folder::markModified));
                folder.markModified();

                if (menu.previousApplication() instanceof ISaveDialogListener listener)
                    listener.acceptSavePath(path.resolve(name));

                menu.syncBlockEntity();
            }
        }
    }
}
