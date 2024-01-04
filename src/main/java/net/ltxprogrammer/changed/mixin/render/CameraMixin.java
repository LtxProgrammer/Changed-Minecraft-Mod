package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Camera;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "setup", at = @At("HEAD"))
    public void setupWithTug(BlockGetter level, Entity entity, boolean firstPerson, boolean mirrored, float partialTicks, CallbackInfo ci) {
        if (!(entity instanceof Player player))
            return;

        var tug = CameraUtil.getTugData(player);
        if (tug != null) {
            Vec3 direction = player.getLookAngle().lerp(tug.getDirection(player, partialTicks), tug.strength);
            player.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition().add(direction));
            player.yRotO = player.getYRot();
            player.xRotO = player.getXRot();
        }
    }

    @Inject(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    protected void setPosition(Vec3 vec, CallbackInfo callbackInfo) {
        Camera self = (Camera)(Object)this;
        if (self.getEntity() instanceof Player player && !player.isSpectator()) {
            ProcessTransfur.ifPlayerLatex(player, variant -> {
                float z = variant.getParent().cameraZOffset;
                var look = self.getLookVector().copy();
                if (Math.abs(look.x()) < 0.0001f && Math.abs(look.z()) < 0.0001f)
                    look = self.getUpVector().copy();
                look.mul(1.0f, 0.0f, 1.0f);
                look.normalize();
                var newVec = vec.add(look.x() * z, 0.0f, look.z() * z);
                self.position = newVec;
                self.blockPosition.set(newVec.x, newVec.y, newVec.z);
                callbackInfo.cancel();
            });
        }
    }
}
