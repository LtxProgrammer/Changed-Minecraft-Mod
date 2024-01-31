package net.ltxprogrammer.changed.entity.beast;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.ltxprogrammer.changed.ability.IAbstractLatex;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LatexHuman extends LatexEntity {
    protected static final EntityDataAccessor<Optional<UUID>> DATA_PLAYER = SynchedEntityData.defineId(LatexHuman.class, EntityDataSerializers.OPTIONAL_UUID);

    public LatexHuman(EntityType<? extends LatexHuman> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PLAYER, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.entityData.get(DATA_PLAYER).ifPresent(uuid -> {
            tag.putUUID("RepresentPlayer", uuid);
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("RepresentPlayer")) {
            this.entityData.set(DATA_PLAYER, Optional.of(tag.getUUID("RepresentPlayer")));
        }
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.getAll();
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.DARK;
    }

    public UUID getRepresentUUID() {
        return this.entityData.get(DATA_PLAYER).orElseGet(() -> {
            if (this.getUnderlyingPlayer() != null) {
                return this.getUnderlyingPlayer().getUUID();
            } else {
                return this.getUUID();
            }
        });
    }

    public Optional<GameProfile> getGameProfile() {
        return this.entityData.get(DATA_PLAYER).map(uuid -> new GameProfile(uuid, null));
    }

    private boolean pendingTextures = false;
    @Nullable
    public UUID modelUUID = null;
    @Nullable
    public ResourceLocation skinTextureLocation = null;
    @Nullable
    public String modelName = null;

    public void setRepresentPlayer(@Nullable UUID uuid) {
        this.entityData.set(DATA_PLAYER, Optional.ofNullable(uuid));
    }

    @OnlyIn(Dist.CLIENT)
    protected void registerTextures() {
        var profile = getGameProfile();
        if (profile.isEmpty())
            return;
        if (profile.get().getId().equals(modelUUID)) {
            if (skinTextureLocation != null && modelName != null)
                return;
        }

        synchronized(this) {
            if (!this.pendingTextures) {
                this.pendingTextures = true;
                Minecraft.getInstance().getSkinManager().registerSkins(profile.get(), (type, location, profileTexture) -> {
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        this.modelUUID = profile.get().getId();
                        this.skinTextureLocation = location;
                        this.modelName = profileTexture.getMetadata("model");
                        if (this.modelName == null) {
                            this.modelName = "default";
                        }
                    }
                }, true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getSkinTextureLocation() {
        this.registerTextures();

        if (skinTextureLocation != null)
            return skinTextureLocation;
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getSkinTextureLocation();
        return DefaultPlayerSkin.getDefaultSkin(this.getRepresentUUID());
    }

    @OnlyIn(Dist.CLIENT)
    public String getModelName() {
        this.registerTextures();

        if (modelName != null)
            return modelName;
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getModelName();
        return DefaultPlayerSkin.getSkinModelName(this.getUUID());
    }

    @Override
    public float getEyeHeightMul() {
        return 0.9F;
    }

    @Override
    public void onReplicateOther(IAbstractLatex other, LatexVariant<?> variant) {
        super.onReplicateOther(other, variant);
        if (this.getUUID() != this.getRepresentUUID()) return;

        if (variant == LatexVariant.LATEX_HUMAN) {
            if (other.getLatexEntity() instanceof LatexHuman human) {
                human.setRepresentPlayer(this.getRepresentUUID());
            }
        }
    }
}
