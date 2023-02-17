package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexSnowLeopardFemale extends AbstractSnowLeopard {
    public LatexSnowLeopardFemale(EntityType<? extends LatexSnowLeopardFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
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
        return HairStyle.Collections.FEMALE_SHADED;
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }
}
