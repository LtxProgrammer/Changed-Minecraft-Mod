package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractGooMantaRay extends AbstractAquaticGenderedEntity {
    public AbstractGooMantaRay(EntityType<? extends AbstractGooMantaRay> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public GooType getGooType() {
        return GooType.NEUTRAL;
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
