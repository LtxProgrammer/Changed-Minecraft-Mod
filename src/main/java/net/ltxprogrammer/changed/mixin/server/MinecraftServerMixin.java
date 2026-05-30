package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.data.PackExtender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @WrapOperation(method = "configurePackRepository",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/packs/repository/PackRepository;getAvailablePacks()Ljava/util/Collection;")
    )
    private static Collection<Pack> doNotIncludeChangedPacks(PackRepository instance, Operation<Collection<Pack>> original) {
        return original.call(instance).stream().filter(pack -> {
            if (pack instanceof PackExtender ext)
                return ext.includeByDefault();
            else
                return true;
        }).toList();
    }
}
