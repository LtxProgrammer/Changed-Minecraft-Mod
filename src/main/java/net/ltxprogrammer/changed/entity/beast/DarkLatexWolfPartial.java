package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ComplexRenderer;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DarkLatexWolfPartial extends AbstractDarkLatexEntity implements ComplexRenderer {
    public DarkLatexWolfPartial(EntityType<? extends DarkLatexWolfPartial> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
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

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        return false;
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

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.DARK;
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.025);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.975);
    }
}
