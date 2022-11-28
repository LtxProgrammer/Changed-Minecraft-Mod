package net.ltxprogrammer.changed.mixin;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    protected void setPosition(Vec3 vec, CallbackInfo callbackInfo) {
        Camera self = (Camera)(Object)this;
        if (self.getEntity() instanceof Player player && !player.isSpectator() && ProcessTransfur.isPlayerLatex(player)) {
            float z = ProcessTransfur.getPlayerLatexVariant(player).cameraZOffset;
            var look = self.getLookVector().copy();
            look.mul(1.0f, 0.0f, 1.0f);
            look.normalize();
            var newVec = vec.add(look.x() * z, 0.0f, look.z() * z);
            self.position = newVec;
            self.blockPosition.set(newVec.x, newVec.y, newVec.z);
            callbackInfo.cancel();
        }
    }
}
