package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexSniperDog extends AbstractLatexWolf {
    public LatexSniperDog(EntityType<? extends LatexSniperDog> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
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
        return layer == 0 ? Color3.getColor("#eb8c44") : Color3.getColor("#894633");
    }

    public Color3 getDripColor() {
        return Color3.getColor("#ef8f44");
    }

}