package net.ltxprogrammer.changed.mixin.compatibility.WATUT;

import com.corosus.watut.PlayerStatusManagerClient;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerStatusManagerClient.class, remap = false)
public class PlayerStatusManagerClientMixin {
    @Inject(method = "getParticlePosition", at = @At("RETURN"), cancellable = true)
    public void overrideForModelSize(Player player, CallbackInfoReturnable<Vec3> cir) {
        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            final double deltaEyeHeight = player.getEyeHeight() - (EntityType.PLAYER.getHeight() * 0.85F);
            cir.setReturnValue(cir.getReturnValue().add(0.0, deltaEyeHeight, 0.0));
        });
    }
}
