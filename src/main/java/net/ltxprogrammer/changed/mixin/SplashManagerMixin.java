package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Mixin(SplashManager.class)
public abstract class SplashManagerMixin extends SimplePreparableReloadListener<List<String>> {
    @Unique
    private static final ResourceLocation CHANGED_SPLASHES_LOCATION = Changed.modResource("texts/splashes.txt");

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true)
    protected void prepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> callback) {
        List<String> newList = new ArrayList<>(callback.getReturnValue());

        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(CHANGED_SPLASHES_LOCATION);

            try {
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

                try {
                    newList.addAll(bufferedreader.lines().map(String::trim).toList());
                } catch (Throwable throwable2) {
                    try {
                        bufferedreader.close();
                    } catch (Throwable throwable1) {
                        throwable2.addSuppressed(throwable1);
                    }

                    throw throwable2;
                }

                bufferedreader.close();
            } catch (Throwable throwable3) {
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (Throwable throwable) {
                        throwable3.addSuppressed(throwable);
                    }
                }

                throw throwable3;
            }

            if (resource != null) {
                resource.close();
            }

            callback.setReturnValue(newList);
            return;
        } catch (IOException ioexception) {
            return;
        }
    }
}
