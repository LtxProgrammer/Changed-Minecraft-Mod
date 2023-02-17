package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexSniperDog extends AbstractLatexWolf {
    public LatexSniperDog(EntityType<? extends LatexSniperDog> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.getColor("#eb8c44");
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collections.NONE;
    }

    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#ef8f44");
    }

}