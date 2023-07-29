package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractLatexMantaRay extends AbstractAquaticGenderedEntity {
    public AbstractLatexMantaRay(EntityType<? extends AbstractLatexMantaRay> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.getColor("#6f7696");
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.getColor("#d2d9e1");
    }
}
