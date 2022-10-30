package net.ltxprogrammer.changed.util;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureUtil {
    public static AbstractTexture merge(AbstractTexture base, AbstractTexture overlay) {
        //base.load();
        return overlay;
    }
}
