package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class EyeStyle extends ForgeRegistryEntry<EyeStyle> {
    public static final DeferredRegister<EyeStyle> REGISTRY = ChangedRegistry.EYE_STYLE.createDeferred(Changed.MODID);

    private final ResourceLocation name;
    private final ResourceLocation iris;
    private final ResourceLocation sclera;

    public EyeStyle(ResourceLocation name) {
        this.name = name;
        this.iris = new ResourceLocation(name.getNamespace(), "textures/eyes/" + name.getPath() + "_iris.png");
        this.sclera = new ResourceLocation(name.getNamespace(), "textures/eyes/" + name.getPath() + "_sclera.png");
    }

    public ResourceLocation getIris() {
        return iris;
    }

    public ResourceLocation getSclera() {
        return sclera;
    }

    private static RegistryObject<EyeStyle> register(String name) {
        return REGISTRY.register(name, () -> new EyeStyle(Changed.modResource(name)));
    }

    public static final RegistryObject<EyeStyle> V1 = register("v1");
    public static final RegistryObject<EyeStyle> V2 = register("v2");
    public static final RegistryObject<EyeStyle> DICHROME = register("dichrome");

    public Component getName() {
        return new TranslatableComponent("eyestyle." + name.getNamespace() + "." + name.getPath());
    }
}
