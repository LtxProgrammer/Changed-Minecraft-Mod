package net.ltxprogrammer.changed.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.network.packet.MountTransfurPacket;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements PlayerDataExtension {
    public ServerPlayerMixin(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_) {
        super(p_36114_, p_36115_, p_36116_, p_36117_);
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    public void restoreFrom(ServerPlayer player, boolean restore, CallbackInfo callbackInfo) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        if (player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM) || restore) {
            ProcessTransfur.ifPlayerTransfurred(player, oldVariant -> {
                if (!oldVariant.willSurviveTransfur)
                    return;
                if (!restore && oldVariant.getParent().is(ChangedTags.TransfurVariants.TEMPORARY_ONLY))
                    return; // Exception to keepForm gamerule

                var newVariant = ProcessTransfur.setPlayerTransfurVariant(self, oldVariant.getParent(), oldVariant.transfurContext, oldVariant.transfurProgression);
                if (newVariant == null)
                    return;
                newVariant.load(oldVariant.save());
                newVariant.getChangedEntity().readPlayerVariantData(oldVariant.getChangedEntity().savePlayerVariantData());
                newVariant.handleRespawn();
            });
        }
    }

    @Unique
    private void readTransfurVariant(CompoundTag tag) {
        if (tag.contains("TransfurVariant")) {
            ResourceLocation variantId = TagUtil.getResourceLocation(tag, "TransfurVariant");
            TransfurVariant<?> variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(variantId);
            if (variant == null) {
                Changed.LOGGER.warn("Missing transfur variant registry entry for {}, falling back", variantId);
                variant = ChangedTransfurVariants.FALLBACK_VARIANT.get();
            }
            final TransfurVariantInstance<?> variantInstance = ProcessTransfur.setPlayerTransfurVariant(this, variant, null, 1.0f, false);

            if (variantInstance == null) {
                Changed.LOGGER.warn("Instanced transfur variant is null");
                return;
            }

            if (tag.contains("TransfurVariantData"))
                variantInstance.load(tag.getCompound("TransfurVariantData"));
            else {
                if (tag.contains("TransfurVariantAge"))
                    variantInstance.ageAsVariant = tag.getInt("TransfurVariantAge");
                if (tag.contains("TransfurAbilities"))
                    variantInstance.loadAbilities(tag.getCompound("TransfurAbilities"));
            }

            final var entity = variantInstance.getChangedEntity();
            if (tag.contains("TransfurData"))
                entity.readPlayerVariantData(tag.getCompound("TransfurData"));


            if (tag.contains("Leash", 10))
                entity.setLeashInfoTag(tag.getCompound("Leash"));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("PaleExposure")) {
            Pale.setPaleExposure(this, tag.getInt("PaleExposure"));
        }
        if (tag.contains("TransfurProgress")) {
            if (tag.get("TransfurProgress") instanceof IntTag intTag) { // Adapt to old progress saving method
                ProcessTransfur.setPlayerTransfurProgress(this, (float)intTag.getAsInt() * 0.001f);
            } else if (tag.get("TransfurProgress") instanceof FloatTag floatTag) {
                ProcessTransfur.setPlayerTransfurProgress(this, floatTag.getAsFloat());
            }
        }

        readTransfurVariant(tag);

        if (tag.contains("PlayerMover")) {
            try {
                setPlayerMover(ChangedRegistry.PLAYER_MOVER.get().getValue(TagUtil.getResourceLocation(tag, "PlayerMover")).createInstance());

                if (tag.contains("PlayerMoverInstance")) {
                    getPlayerMover().readFrom(tag.getCompound("PlayerMoverInstance"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("PaleExposure", Pale.getPaleExposure(this));
        tag.putFloat("TransfurProgress", ProcessTransfur.getPlayerTransfurProgress(this));
        ProcessTransfur.ifPlayerTransfurred(this, variant -> {
            TagUtil.putResourceLocation(tag, "TransfurVariant", variant.getFormId());
            tag.put("TransfurVariantData", variant.save());

            var entity = variant.getChangedEntity();
            var entityData = entity.savePlayerVariantData();
            if (!entityData.isEmpty())
                tag.put("TransfurData", entityData);

            if (entity.getLeashHolder() != null) {
                CompoundTag compoundtag2 = new CompoundTag();
                if (entity.getLeashHolder() instanceof LivingEntity) {
                    UUID uuid = entity.getLeashHolder().getUUID();
                    compoundtag2.putUUID("UUID", uuid);
                } else if (entity.getLeashHolder() instanceof HangingEntity) {
                    BlockPos blockpos = ((HangingEntity) entity.getLeashHolder()).getPos();
                    compoundtag2.putInt("X", blockpos.getX());
                    compoundtag2.putInt("Y", blockpos.getY());
                    compoundtag2.putInt("Z", blockpos.getZ());
                }

                tag.put("Leash", compoundtag2);
            } else if (entity.getLeashInfoTag() != null) {
                tag.put("Leash", entity.getLeashInfoTag().copy());
            }
        });
        var mover = getPlayerMover();
        if (mover != null) {
            try {
                TagUtil.putResourceLocation(tag, "PlayerMover", ChangedRegistry.PLAYER_MOVER.get().getKey(mover.parent));
                var moverTag = new CompoundTag();
                mover.saveTo(moverTag);
                tag.put("PlayerMoverInstance", moverTag);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    public void stopRiding(CallbackInfo callbackInfo) {
        if (this.getVehicle() instanceof Player)
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new MountTransfurPacket(getUUID(), getUUID()));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (getPlayerMover() != null) {
            getPlayerMover().aiStep(this, InputWrapper.from(this), LogicalSide.SERVER);
        }
    }

    @Override
    protected void serverAiStep() {
        super.serverAiStep();
        if (getPlayerMover() != null) {
            getPlayerMover().serverAiStep(this, InputWrapper.from(this), LogicalSide.SERVER);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callback) {
        var playerMover = getPlayerMover();
        if (playerMover != null && playerMover.shouldRemoveMover(this, InputWrapper.from(this), LogicalSide.SERVER))
            setPlayerMover(null);
    }
}
