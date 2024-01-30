package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DarkLatexWolfPartial extends AbstractDarkLatexEntity {
    public DarkLatexWolfPartial(EntityType<? extends DarkLatexWolfPartial> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.DARK;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.getAll();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.DARK;
    }

    @Override
    public boolean isMaskless() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getSkinTextureLocation() {
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getSkinTextureLocation();
        return DefaultPlayerSkin.getDefaultSkin(this.getUUID());
    }

    @OnlyIn(Dist.CLIENT)
    public String getModelName() {
        if (getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
            return clientPlayer.getModelName();
        return DefaultPlayerSkin.getSkinModelName(this.getUUID());
    }
}
