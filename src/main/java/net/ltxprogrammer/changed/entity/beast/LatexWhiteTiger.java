package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class LatexWhiteTiger extends LatexEntity implements PowderSnowWalkable {
    public LatexWhiteTiger(EntityType<? extends LatexWhiteTiger> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.MALE.getStyles();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }
}