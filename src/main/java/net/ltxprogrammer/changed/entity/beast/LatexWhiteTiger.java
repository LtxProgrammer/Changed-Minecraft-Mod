package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class LatexWhiteTiger extends LatexEntity implements PowderSnowWalkable {
    public LatexWhiteTiger(EntityType<? extends LatexWhiteTiger> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.BALD, HairStyle.MOHAWK_S, HairStyle.HEAD_FUZZ_S, HairStyle.MALE_SHORT_FRONT_S, HairStyle.MALE_STANDARD_S, HairStyle.MALE_SIDEBURN_S);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }
}