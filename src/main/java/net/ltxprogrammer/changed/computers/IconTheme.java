package net.ltxprogrammer.changed.computers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;

public enum IconTheme implements StringRepresentable, IExtensibleEnum {
    STANDARD("standard");

    private final String serialName;

    IconTheme(String serialName) {
        this.serialName = serialName;
    }

    public static IconTheme create(String enumName, String serialName) {
        throw new IllegalStateException("enum not extended");
    }

    @Override
    public String getSerializedName() {
        return serialName;
    }

    public ResourceLocation getIconLocation(ResourceLocation icon) {
        return ResourceLocation.fromNamespaceAndPath(
                icon.getNamespace(),
                "textures/gui/computer/themes/%s/%s.png".formatted(this.getSerializedName(), icon.getPath())
        );
    }
}
