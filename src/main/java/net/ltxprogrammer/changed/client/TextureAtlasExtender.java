package net.ltxprogrammer.changed.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.stream.Stream;

public interface TextureAtlasExtender {
    Stream<TextureAtlasSprite> getSprites();

    int getWidth();
    int getHeight();
}
