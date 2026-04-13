package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.init.ChangedApplications;

import java.util.List;

public class FileExplorerApplication implements Application {
    public FileExplorerApplication(List<String> args) {

    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.FILE_EXPLORER.get();
    }
}
