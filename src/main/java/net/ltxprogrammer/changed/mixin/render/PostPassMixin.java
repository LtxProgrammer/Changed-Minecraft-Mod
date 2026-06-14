package net.ltxprogrammer.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.client.LocalTransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PostPass.class)
public abstract class PostPassMixin {
    @WrapOperation(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EffectInstance;apply()V"))
    public void changed$setChangedUniforms(EffectInstance instance, Operation<Void> original) {
        var localPlayer = UniversalDist.getLocalPlayer();
        var variantInstance = ProcessTransfur.getPlayerTransfurVariant(localPlayer);
        if (!(variantInstance instanceof LocalTransfurVariantInstance<?> localVariant)) {
            instance.safeGetUniform("changed_PostChainStrength").set(0.0f);
            original.call(instance);
            return;
        }

        instance.safeGetUniform("changed_PostChainStrength").set(localVariant.getPostChainStrength(Minecraft.getInstance().getPartialTick()));

        original.call(instance);
    }
}
