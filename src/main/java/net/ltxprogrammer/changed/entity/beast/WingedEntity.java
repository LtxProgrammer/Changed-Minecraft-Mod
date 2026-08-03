package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.IExtensibleEnum;

public interface WingedEntity {
    ResourceLocation WEBBED_BACKGROUND = Changed.modResource("webbed_background");

    enum WingDesign implements IExtensibleEnum {
        WEBBED_DARK(WEBBED_BACKGROUND, Changed.modResource("webbed_dark")),
        WEBBED_GOLDEN(WEBBED_BACKGROUND, Changed.modResource("webbed_golden")),
        WEBBED_PINK(WEBBED_BACKGROUND, Changed.modResource("webbed_pink")),
        WEBBED_RED(WEBBED_BACKGROUND, Changed.modResource("webbed_red"));

        public final ResourceLocation background;
        public final ResourceLocation foreground;

        WingDesign(ResourceLocation background, ResourceLocation foreground) {
            this.background = ResourceLocation.fromNamespaceAndPath(background.getNamespace(), "textures/gui/flight_stamina/" + background.getPath() + ".png");
            this.foreground = ResourceLocation.fromNamespaceAndPath(foreground.getNamespace(), "textures/gui/flight_stamina/" + foreground.getPath() + ".png");
        }

        public static WingDesign create(String name, ResourceLocation background, ResourceLocation foreground) {
            throw new IllegalStateException("enum not extended");
        }
    }

    WingDesign getWingDesign();
}
