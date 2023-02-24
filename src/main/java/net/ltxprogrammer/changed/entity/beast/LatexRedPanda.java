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


public class LatexRedPanda extends LatexEntity {
    public LatexRedPanda(EntityType<? extends LatexRedPanda> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor(this.random.nextInt(4) < 3 ? "#bb2a3b" : "#5a2b49");
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return layer == 0 ? ChangedParticles.Color3.getColor("#bb2a3b") : ChangedParticles.Color3.getColor("#ffffff");
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.SIDE_TUFTS;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collections.MALE_NO_WOLF_EARS;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }


    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }
}