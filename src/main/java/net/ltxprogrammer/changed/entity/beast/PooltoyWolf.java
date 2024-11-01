package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PooltoyWolf extends AbstractPooltoy {
    public static final float SCALE = 1.3F;

    public PooltoyWolf(EntityType<? extends PooltoyWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.2);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.95);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(4.0);
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.SHORT_MESSY.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.getAll();
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.fromInt(0x50c3ff);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }
}
