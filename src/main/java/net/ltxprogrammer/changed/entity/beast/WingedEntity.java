package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.IExtensibleEnum;

public interface WingedEntity {
    ResourceLocation WEBBED_BACKGROUND = Changed.modResource("webbed_background");

    record WingDesign(ResourceLocation backgroundTexture, ResourceLocation foregroundTexture) {
        public static final WingDesign WEBBED_DARK = forTextureIDs(WEBBED_BACKGROUND, Changed.modResource("webbed_dark"));
        public static final WingDesign WEBBED_GOLDEN = forTextureIDs(WEBBED_BACKGROUND, Changed.modResource("webbed_golden"));
        public static final WingDesign WEBBED_PINK = forTextureIDs(WEBBED_BACKGROUND, Changed.modResource("webbed_pink"));
        public static final WingDesign WEBBED_RED = forTextureIDs(WEBBED_BACKGROUND, Changed.modResource("webbed_red"));

        public static WingDesign forTextureIDs(ResourceLocation background, ResourceLocation foreground) {
            return new WingDesign(
                    ResourceLocation.fromNamespaceAndPath(background.getNamespace(), "textures/gui/flight_stamina/" + background.getPath() + ".png"),
                    ResourceLocation.fromNamespaceAndPath(foreground.getNamespace(), "textures/gui/flight_stamina/" + foreground.getPath() + ".png")
            );
        }

        public static WingDesign create(String name, ResourceLocation background, ResourceLocation foreground) {
            throw new IllegalStateException("enum not extended");
        }
    }

    WingDesign getWingDesign();
}
