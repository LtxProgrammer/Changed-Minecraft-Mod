package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.generator.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedFileSystemGenerators {
    public static DeferredRegister<Codec<? extends FileSystemGenerator>> REGISTRY = ChangedRegistry.FILE_SYSTEM_GENERATORS.createDeferred(Changed.MODID);

    public static final RegistryObject<Codec<ComputerFileSystemGenerator>> COMPUTER = REGISTRY.register("computer", () -> ComputerFileSystemGenerator.CODEC);
    public static final RegistryObject<Codec<DiskFileSystemGenerator>> DISK = REGISTRY.register("disk", () -> DiskFileSystemGenerator.CODEC);
    public static final RegistryObject<Codec<ServerFileSystemGenerator>> SERVER = REGISTRY.register("server", () -> ServerFileSystemGenerator.CODEC);
}
