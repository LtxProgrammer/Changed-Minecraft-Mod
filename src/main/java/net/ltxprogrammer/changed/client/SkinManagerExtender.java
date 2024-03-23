package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface SkinManagerExtender {
    Optional<NativeImage> getSkinImage(ResourceLocation name);
}
