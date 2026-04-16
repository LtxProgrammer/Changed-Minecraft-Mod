package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ApplicationType<T extends Application> {
    protected ResourceLocation registryName;

    protected ResourceLocation getRegistryName() {
        if (registryName != null)
            return registryName;
        registryName = ChangedRegistry.APPLICATION_TYPES.getKey(this);
        return registryName;
    }

    protected final Cacheable<Component> displayName = Cacheable.of(() -> {
        var regName = this.getRegistryName();
        return Component.translatable("application.%s.%s".formatted(regName.getNamespace(), regName.getPath()));
    });

    protected final Cacheable<ResourceLocation> iconLocation = Cacheable.of(() -> {
        var regName = this.getRegistryName();
        return ResourceLocation.fromNamespaceAndPath(regName.getNamespace(), "app/%s".formatted(regName.getPath()));
    });

    public ApplicationType(ApplicationConstructor<T> applicationConstructor) {
        this.applicationConstructor = applicationConstructor;
    }

    public Component getDisplayName() {
        return displayName.get();
    }

    public ResourceLocation getIconLocation() {
        return iconLocation.get();
    }

    public interface ApplicationConstructor<T extends Application> {
        T createApplication(ComputerMenu menu, List<String> args);
    }

    private final ApplicationConstructor<T> applicationConstructor;

    public T createApplication(ComputerMenu menu, List<String> args) {
        return applicationConstructor.createApplication(menu, args);
    }
}
