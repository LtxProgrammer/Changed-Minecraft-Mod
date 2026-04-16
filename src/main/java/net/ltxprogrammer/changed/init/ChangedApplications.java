package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.application.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedApplications {
    public static final DeferredRegister<ApplicationType<?>> REGISTRY = ChangedRegistry.APPLICATION_TYPES.createDeferred(Changed.MODID);

    public static final RegistryObject<ApplicationType<DesktopApplication>> DESKTOP = REGISTRY.register("desktop", () -> new ApplicationType<>(DesktopApplication::new));
    public static final RegistryObject<ApplicationType<FileExplorerApplication>> FILE_EXPLORER = REGISTRY.register("file_explorer", () -> new ApplicationType<>(FileExplorerApplication::new));
    public static final RegistryObject<ApplicationType<DoorControllerApplication>> DOOR_CONTROLLER = REGISTRY.register("door_controller", () -> new ApplicationType<>(DoorControllerApplication::new));
}
