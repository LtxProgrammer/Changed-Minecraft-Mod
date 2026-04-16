package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;

import java.util.List;

public class DesktopApplication implements Application {
    public DesktopApplication(ComputerMenu menu, List<String> args) {

    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.DESKTOP.get();
    }
}
