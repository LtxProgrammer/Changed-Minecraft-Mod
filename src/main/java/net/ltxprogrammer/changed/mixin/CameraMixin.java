package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
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
    public void setup(BlockGetter level, Entity entity, boolean p_90578_, boolean p_90579_, float p_90580_, CallbackInfo callback) {
        if (entity instanceof Player player) {
            var tug = CameraUtil.getTugData(player);
            if (tug == null)
                return;
            if (tug.tickExpire() < player.tickCount) {
                CameraUtil.resetTugData(player);
                return;
            }

            float xRotO = player.xRotO;
            float yRotO = player.yRotO;
            Vec3 direction = player.getLookAngle().lerp(tug.getDirection(player), tug.strength());
            player.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition().add(direction));
            player.xRotO = xRotO;
            player.yRotO = yRotO;
        }
    }

    @Inject(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    protected void setPosition(Vec3 vec, CallbackInfo callbackInfo) {
        Camera self = (Camera)(Object)this;
        if (self.getEntity() instanceof Player player && !player.isSpectator() && ProcessTransfur.isPlayerLatex(player)) {
            float z = ProcessTransfur.getPlayerLatexVariant(player).cameraZOffset;
            var look = self.getLookVector().copy();
            if (Math.abs(look.x()) < 0.0001f && Math.abs(look.z()) < 0.0001f)
                look = self.getUpVector().copy();
            look.mul(1.0f, 0.0f, 1.0f);
            look.normalize();
            var newVec = vec.add(look.x() * z, 0.0f, look.z() * z);
            self.position = newVec;
            self.blockPosition.set(newVec.x, newVec.y, newVec.z);
            callbackInfo.cancel();
        }
    }
}
