package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.application.Application;
import net.ltxprogrammer.changed.computers.application.ApplicationType;

import java.util.HashMap;
import java.util.Map;

public abstract class ApplicationScreens {
    public interface ScreenConstructor<T extends Application> {
        ApplicationScreen createScreen(T application, ComputerScreen screen);
    }

    private static final Map<ApplicationType<?>, ScreenConstructor<?>> APPLICATION_SCREENS = new HashMap<>();

    public static <T extends Application> void register(ApplicationType<T> applicationType, ScreenConstructor<T> screenConstructor) {
        APPLICATION_SCREENS.put(applicationType, screenConstructor);
    }

    public static <T extends Application> ApplicationScreen createScreen(T application, ComputerScreen screen) {
        ScreenConstructor constructor = APPLICATION_SCREENS.get(application.getType());
        return constructor.createScreen(application, screen);
    }
}
