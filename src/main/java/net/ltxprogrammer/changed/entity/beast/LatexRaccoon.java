package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class LatexRaccoon extends LatexEntity {
    public LatexRaccoon(EntityType<? extends LatexRaccoon> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#949494");
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.MALE.getStyles();
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return layer == 0 ? ChangedParticles.Color3.getColor("#949494") : ChangedParticles.Color3.getColor("#fafafa");
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }
}