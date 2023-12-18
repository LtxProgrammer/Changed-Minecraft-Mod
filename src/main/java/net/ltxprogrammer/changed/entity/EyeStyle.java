package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.NotImplementedException;

public enum EyeStyle implements IExtensibleEnum {
    V1(Changed.modResource("v1")),
    V2(Changed.modResource("v2")),
    TALL(Changed.modResource("tall"));

    private final ResourceLocation name;
    private final ResourceLocation iris;
    private final ResourceLocation sclera;
    private final Component textName;

    EyeStyle(ResourceLocation name) {
        this.name = name;
        this.iris = new ResourceLocation(name.getNamespace(), "textures/eyes/" + name.getPath() + "_iris.png");
        this.sclera = new ResourceLocation(name.getNamespace(), "textures/eyes/" + name.getPath() + "_sclera.png");
        this.textName = new TranslatableComponent("eyestyle." + name.getNamespace() + "." + name.getPath());
    }

    public ResourceLocation getIris() {
        return iris;
    }

    public ResourceLocation getSclera() {
        return sclera;
    }

    public Component getName() {
        return textName;
    }

    public static EyeStyle create(String name, ResourceLocation fullName) {
        throw new NotImplementedException("Not implemented!");
    }
}
