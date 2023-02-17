package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexMermaidShark extends AbstractGenderedLatexShark {
    public LatexMermaidShark(EntityType<? extends LatexMermaidShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    @Override
    public Gender getGender() {
        return Gender.MALE;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.MALE_NWE_S;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.BALD, HairStyle.MOHAWK_S, HairStyle.HEAD_FUZZ_S, HairStyle.MALE_SHORT_FRONT_S, HairStyle.MALE_STANDARD_S, HairStyle.MALE_SIDEBURN_S);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public boolean isVisuallySwimming() {
        if (this.getUnderlyingPlayer() != null && this.getUnderlyingPlayer().isEyeInFluid(FluidTags.WATER))
            return true;
        return this.isEyeInFluid(FluidTags.WATER) || super.isVisuallySwimming();
    }
}
