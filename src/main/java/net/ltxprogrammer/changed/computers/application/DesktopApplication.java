package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.init.ChangedApplications;

import java.util.List;

public class DesktopApplication implements Application {
    public DesktopApplication(List<String> args) {

    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.DESKTOP.get();
    }
}
