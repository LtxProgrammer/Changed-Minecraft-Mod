package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DarkLatexYufeng extends AbstractDarkLatexEntity {
    public DarkLatexYufeng(EntityType<? extends DarkLatexYufeng> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.MALE_NWE_S;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collections.MALE_NO_WOLF_EARS_SHADED;
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.DARK;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.DARK;
    }
}
