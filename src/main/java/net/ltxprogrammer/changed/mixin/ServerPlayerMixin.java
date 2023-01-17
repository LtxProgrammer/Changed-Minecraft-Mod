package net.ltxprogrammer.changed.mixin;

import com.mojang.authlib.GameProfile;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.network.packet.MountLatexPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.inventory.SpecialLoadingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
        if ((player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM) || restore) && ProcessTransfur.isPlayerLatex(player)) {
            LatexVariant<?> oldVariant = ProcessTransfur.getPlayerLatexVariant(player);
            CompoundTag tag = new CompoundTag();
            oldVariant.abilityInstances.forEach((name, instance) -> {
                CompoundTag abilityTag = new CompoundTag();
                instance.saveData(abilityTag);
                if (!abilityTag.isEmpty())
                    tag.put(name.toString(), abilityTag);
            });

            ProcessTransfur.setPlayerLatexVariant(self, oldVariant.clone());
            LatexVariant<?> newVariant = ProcessTransfur.getPlayerLatexVariant(player);
            newVariant.abilityInstances.forEach((name, instance) -> {
                if (!tag.contains(name.toString()))
                    return;
                CompoundTag abilityTag = tag.getCompound(name.toString());
                instance.readData(abilityTag);
            });
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("TransfurProgress") && tag.contains("TransfurProgressType"))
            ProcessTransfur.setPlayerTransfurProgress(this,
                    new ProcessTransfur.TransfurProgress(tag.getInt("TransfurProgress"),
                            new ResourceLocation(tag.getString("TransfurProgressType"))));
        if (tag.contains("LatexVariant")) {
            ProcessTransfur.setPlayerLatexVariantNamed(this, TagUtil.getResourceLocation(tag, "LatexVariant"));
        }

        LatexVariant<?> variant = ProcessTransfur.getPlayerLatexVariant(this);
        if (variant != null && tag.contains("LatexAbilities")) {
            CompoundTag tagAbilities = tag.getCompound("LatexAbilities");
            tagAbilities.getAllKeys().forEach(key -> {
                ResourceLocation name = ResourceLocation.tryParse(key);
                if (variant.abilityInstances.containsKey(name))
                    variant.abilityInstances.get(name).readData(tagAbilities.getCompound(key));
            });
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("TransfurProgress", ProcessTransfur.getPlayerTransfurProgress(this).ticks());
        tag.putString("TransfurProgressType", ProcessTransfur.getPlayerTransfurProgress(this).type().toString());
        LatexVariant<?> latexVariant = ProcessTransfur.getPlayerLatexVariant(this);
        if (latexVariant != null) {
            TagUtil.putResourceLocation(tag, "LatexVariant", latexVariant.getFormId());
            CompoundTag tagAbilities = new CompoundTag();
            latexVariant.abilityInstances.forEach((name, ability) -> {
                CompoundTag tagAbility = new CompoundTag();
                ability.saveData(tagAbility);
                if (!tagAbility.isEmpty())
                    tagAbilities.put(name.toString(), tagAbility);
            });
            tag.put("LatexAbilities", tagAbilities);
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    public void stopRiding(CallbackInfo callbackInfo) {
        if (this.getVehicle() instanceof Player)
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new MountLatexPacket(getUUID(), getUUID()));
    }
}
