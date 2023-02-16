package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexTigerShark extends AbstractAquaticEntity {
    public LatexTigerShark(EntityType<? extends LatexTigerShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.MALE_NWE;
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }
}
