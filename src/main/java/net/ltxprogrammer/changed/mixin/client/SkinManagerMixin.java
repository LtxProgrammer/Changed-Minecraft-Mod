package net.ltxprogrammer.changed.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.ltxprogrammer.changed.client.SkinManagerExtender;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

@Mixin(SkinManager.class)
public abstract class SkinManagerMixin implements SkinManagerExtender {
    @Shadow @Final private File skinsDirectory;

    public Optional<NativeImage> getSkinImage(ResourceLocation name) {
        if (!name.getNamespace().equals("minecraft"))
            return Optional.empty();
        if (!name.getPath().startsWith("skins/"))
            return Optional.empty();
        String hashString = name.getPath().substring(6);
        File parentDir = new File(this.skinsDirectory, hashString.length() > 2 ? hashString.substring(0, 2) : "xx");
        File imageFile = new File(parentDir, hashString);

        FileInputStream content = null;
        NativeImage image = null;
        try {
            content = new FileInputStream(imageFile);
            image = NativeImage.read(content);
        } catch (Exception ignored) {}
        finally {
            try {
                if (content != null)
                    content.close();
            } catch (Exception ignored) {}
        }

        return Optional.ofNullable(image);
    }
}
