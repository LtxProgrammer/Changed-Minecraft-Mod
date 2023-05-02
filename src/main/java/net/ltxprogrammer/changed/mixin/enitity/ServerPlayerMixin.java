package net.ltxprogrammer.changed.mixin.enitity;

import com.mojang.authlib.GameProfile;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.network.packet.MountLatexPacket;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.inventory.SpecialLoadingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_) {
        super(p_36114_, p_36115_, p_36116_, p_36117_);
    }

    @Inject(method = "initMenu", at = @At("TAIL"))
    public void initMenu(AbstractContainerMenu p_143400_, CallbackInfo ci) {
        if (p_143400_ instanceof SpecialLoadingMenu special)
            special.afterInit(p_143400_);
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    public void restoreFrom(ServerPlayer player, boolean restore, CallbackInfo callbackInfo) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        if (player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM) || restore) {
            ProcessTransfur.ifPlayerLatex(player, oldVariant -> {
                ProcessTransfur.setPlayerLatexVariant(self, oldVariant.getParent())
                        .loadAbilities(oldVariant.saveAbilities());
            });
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("PaleExposure")) {
            Pale.setPaleExposure(this, tag.getInt("PaleExposure"));
        }
        if (tag.contains("TransfurProgress") && tag.contains("TransfurProgressType")) {
            if (tag.get("TransfurProgress") instanceof IntTag intTag) { // Adapt to old progress saving method
                ProcessTransfur.setPlayerTransfurProgress(this,
                        new ProcessTransfur.TransfurProgress((float)intTag.getAsInt() * 0.001f,
                                new ResourceLocation(tag.getString("TransfurProgressType"))));
            } else if (tag.get("TransfurProgress") instanceof FloatTag floatTag) {
                ProcessTransfur.setPlayerTransfurProgress(this,
                        new ProcessTransfur.TransfurProgress(floatTag.getAsFloat(),
                                new ResourceLocation(tag.getString("TransfurProgressType"))));
            }
        }
        if (tag.contains("LatexVariant")) {
            ProcessTransfur.setPlayerLatexVariantNamed(this, TagUtil.getResourceLocation(tag, "LatexVariant"));
        }

        ProcessTransfur.ifPlayerLatex(this, variant -> {
            if (tag.contains("LatexAbilities"))
                variant.loadAbilities(tag.getCompound("LatexAbilities"));
        });
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("PaleExposure", Pale.getPaleExposure(this));
        tag.putFloat("TransfurProgress", ProcessTransfur.getPlayerTransfurProgress(this).progress());
        tag.putString("TransfurProgressType", ProcessTransfur.getPlayerTransfurProgress(this).type().toString());
        ProcessTransfur.ifPlayerLatex(this, variant -> {
            TagUtil.putResourceLocation(tag, "LatexVariant", variant.getFormId());
            tag.put("LatexAbilities", variant.saveAbilities());
        });
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    public void stopRiding(CallbackInfo callbackInfo) {
        if (this.getVehicle() instanceof Player)
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new MountLatexPacket(getUUID(), getUUID()));
    }
}
