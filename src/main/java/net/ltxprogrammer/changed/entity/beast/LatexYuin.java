package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class LatexYuin extends LatexEntity implements PowderSnowWalkable, PatronOC {
    public LatexYuin(EntityType<? extends LatexYuin> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#ffffff");
    }


    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }


    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public @NotNull ChangedParticles.Color3 getHairColor() {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.BALD);
    }
}
