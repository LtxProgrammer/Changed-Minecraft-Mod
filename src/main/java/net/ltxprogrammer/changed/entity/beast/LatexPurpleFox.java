package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PatronOC;
import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexPurpleFox extends AbstractLatexWolf implements PowderSnowWalkable, PatronOC {
    public LatexPurpleFox(EntityType<? extends LatexPurpleFox> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Color3 getDripColor() {
        return Color3.getColor("#cebbe8");
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.fromInt(0xfae9fa);
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.SHORT_MESSY.get();
    }
}
