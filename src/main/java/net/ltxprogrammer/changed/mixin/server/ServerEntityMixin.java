package net.ltxprogrammer.changed.mixin.server;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.packet.SetLatexVariantDataPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "sendDirtyEntityData", at = @At("RETURN"))
    private void andSendLatexVariant(CallbackInfo ci) {
        ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(this.entity), variant -> {
            var entity = variant.getLatexEntity();

            SynchedEntityData synchedentitydata = entity.getEntityData();
            if (synchedentitydata.isDirty()) {
                var packet = new SetLatexVariantDataPacket(this.entity.getId(), synchedentitydata, false);
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), packet);
            }
        });
    }
}
