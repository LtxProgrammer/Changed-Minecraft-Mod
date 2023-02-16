package net.ltxprogrammer.changed.entity.beast.boss;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BehemothHandRight extends BehemothHand {
    public BehemothHandRight(EntityType<? extends BehemothHandRight> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    @Override
    public @NotNull ChangedParticles.Color3 getHairColor() {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.BALD);
    }
}
