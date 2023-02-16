package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PatronOC;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexSilverFox extends AbstractLatexWolf implements PatronOC {
    public LatexSilverFox(EntityType<? extends LatexSilverFox> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.SILVER;
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.BALD);
    }
}
