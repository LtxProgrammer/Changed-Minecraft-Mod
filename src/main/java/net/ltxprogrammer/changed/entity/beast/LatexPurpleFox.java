package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PatronOC;
import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexPurpleFox extends AbstractLatexWolf implements PowderSnowWalkable, PatronOC {
    public LatexPurpleFox(EntityType<? extends LatexPurpleFox> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#cebbe8");
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.EMPTY;
    }
}
