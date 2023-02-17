package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexSquidDogFemale extends AbstractLatexSquidDog {
    public LatexSquidDogFemale(EntityType<? extends LatexSquidDogFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.LEGACY_FEMALE_RIGHT_BANG_S;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.LEGACY_FEMALE_DUAL_BANGS_S, HairStyle.FEMALE_NO_BANGS_S, HairStyle.LEGACY_FEMALE_RIGHT_BANG_S,
                HairStyle.LEGACY_FEMALE_LEFT_BANG_S, HairStyle.LEGACY_FEMALE_TRIPLE_BANGS_S, HairStyle.FEMALE_SIDE_BANGS_S);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }
}
