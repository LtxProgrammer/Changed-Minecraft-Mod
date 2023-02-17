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

public class LatexTrafficConeDragon extends LatexEntity {
    public LatexTrafficConeDragon(EntityType<? extends LatexTrafficConeDragon> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.BALD, HairStyle.MOHAWK, HairStyle.HEAD_FUZZ, HairStyle.MALE_SHORT_FRONT, HairStyle.MALE_STANDARD, HairStyle.MALE_SIDEBURN);
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.getColor("#ffd201");
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.YELLOW;
    }
}
