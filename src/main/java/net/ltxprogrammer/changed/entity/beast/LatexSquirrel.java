package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexSquirrel extends LatexEntity {
    public LatexSquirrel(EntityType<? extends LatexSquirrel> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
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
        return HairStyle.Collection.getAll();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return layer == 0 ? Color3.getColor("#ac8f64") : Color3.getColor("#6f482a");
    }

    @Override
    public Color3 getDripColor() {
        return Color3.getColor(this.random.nextInt(4) < 3 ? "#ac8f64" : "#ffe8a5");
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#ac8e63");
    }
}