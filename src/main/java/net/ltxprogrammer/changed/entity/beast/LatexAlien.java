package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class LatexAlien extends ChangedEntity {
    public LatexAlien(EntityType<? extends LatexAlien> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public Color3 getDripColor() {
        return Color3.getColor("#1983a9");
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.getColor("#1983a9");
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.EMPTY;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#1983a9");
    }
}