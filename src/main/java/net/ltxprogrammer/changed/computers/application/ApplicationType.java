package net.ltxprogrammer.changed.computers.application;

import java.util.List;

public class ApplicationType<T extends Application> {
    public ApplicationType(ApplicationConstructor<T> applicationConstructor) {
        this.applicationConstructor = applicationConstructor;
    }

    public interface ApplicationConstructor<T extends Application> {
        T createApplication(List<String> args);
    }

    private final ApplicationConstructor<T> applicationConstructor;

    public T createApplication(List<String> args) {
        return applicationConstructor.createApplication(args);
    }
}
